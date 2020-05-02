package test1;

import com.mysql.cj.util.StringUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;


import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class ReMybatisV2 {
    private Configuration configuration = new Configuration();
    private String namespace;

    /**
     * 加载Xml文件
     */
    private void loadConfiguration() {
        String location = "parse02/mybatis-config.xml";
        InputStream inputStream = getResourceAsStream(location);
        Document document = createDocument(inputStream);
        parseConfiguration(document.getRootElement());

    }


    private void parseConfiguration(Element rootElement) {
        Element envirsElement = rootElement.element("environments");
        parseEnvironments(envirsElement);
        Element mappersElement = rootElement.element("mappers");
        parseMappers(mappersElement);
    }


    private void parseMappers(Element mappersElement) {
        List<Element> mappers = mappersElement.elements("mapper");
        for (Element mapper : mappers) {
            parseMapper(mapper);
        }
    }

    private void parseMapper(Element mapper) {
        String resource = mapper.attributeValue("resource");
        InputStream resourceAsStream = getResourceAsStream(resource);
        Document document = createDocument(resourceAsStream);
        parseXmlMapper(document.getRootElement());
    }


    private void parseXmlMapper(Element rootElement) {
        namespace = rootElement.attributeValue("namespace");
        List<Element> selectElements = rootElement.elements("select");
        for (Element selectElement : selectElements) {
            parseStatementElement(selectElement);
        }

    }

    private void parseStatementElement(Element selectElement) {
        //statementId
        String statementId = selectElement.attributeValue("id");
        if (statementId == null || statementId == "") {
            return;
        }
        //statementType
        String statementType = selectElement.attributeValue("statementType");
        statementType = statementType == null || statementType == "" ? "prepared" : statementType;

        String resultTypeName = selectElement.attributeValue("resultType");
        Class<?> resultTypeClass = null;
        try {
            //resultTypeClass
            resultTypeClass = Class.forName(resultTypeName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SqlSource sqlSource = createSource(selectElement);
        MappedStatement mappedStatement = new
                MappedStatement(statementId,
                sqlSource,
                statementType,
                null,
                resultTypeClass);
        configuration.addMappedStatements(statementId, mappedStatement);
    }

    //TODO SQLSource的封装
    private SqlSource createSource(Element selectElement) {
        return null;
    }


    private void parseEnvironments(Element envirsElement) {
        String defaultId = envirsElement.attributeValue("default");
        List<Element> elements = envirsElement.elements("environment");
        for (Element element : elements) {
            String id = element.attributeValue("id");
            if (id.equals(defaultId)) {
                parseDataSource(element);
            }
        }
    }

    private void parseDataSource(Element element) {
        Element elementDataSource = element.element("dataSource");
        String type = elementDataSource.attributeValue("type");
        if ("DBCP".equals(type)) {
            BasicDataSource dataSource = new BasicDataSource();
            Properties properties = parseProperties(elementDataSource);
            dataSource.setDriverClassName(properties.getProperty("driver"));
            dataSource.setUrl(properties.getProperty("url"));
            dataSource.setUsername(properties.getProperty("username"));
            dataSource.setPassword(properties.getProperty("password"));
            configuration.setDataSource(dataSource);
        }
    }

    private Properties parseProperties(Element elementDataSource) {
        List<Element> propertiesElement = elementDataSource.elements("property");
        Properties properties = new Properties();
        for (Element element : propertiesElement) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.put(name, value);
        }
        return properties;
    }


    private Document createDocument(InputStream inputStream) {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(inputStream);
            return document;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream getResourceAsStream(String location) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location);
        return inputStream;
    }


    @Test
    public void test() {
        loadConfiguration();
        List<Employee> employeeList = selectEmployee("selectEmployee", "10001");
        System.out.println(employeeList);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", "孙尚香");
        param.put("sn", "10002");
        List<Employee> employeeList2 = selectEmployee("selectEmployee", param);
        System.out.println(employeeList2);

        Employee emp = new Employee();
        emp.setSn("10003");
        List<Employee> employeeList3 = selectEmployee("selectEmployee", emp);
        System.out.println(employeeList3);
    }


    public <T> List<T> selectEmployee(String statementId, Object param) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<T> results = new ArrayList<T>();
        try {
            //加载数据库驱动
            //通过驱动管理器获取数据库连接
            connection=getConnection();
            //定义sql语句
//            String sql = properties.getProperty("db." + statementId + ".sql");

            MappedStatement mappedStatement = configuration.getMappedStatementById(statementId);
            SqlSource sqlSource = mappedStatement.getSqlSource();
            //TODO
            BoundSql boundSql = sqlSource.getBoundSql(param);
            String sql = boundSql.getSql();
            //获取预处理statement
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
           setParameters(preparedStatement,param,boundSql);
            //向数据库发起sql执行
            rs = preparedStatement.executeQuery();

            handleResultSet(rs,results,mappedStatement);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //资源释放
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }

    private <T> void handleResultSet(ResultSet rs, List<T> results, MappedStatement mappedStatement) throws  Exception{
        Class<?> returnTypeClass =mappedStatement.getResultTypeClass();
        //结果映射
        while (rs.next()) {
            Object o = returnTypeClass.newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            for (int i = 0; i < count; i++) {
                String columnName = metaData.getColumnName(i + 1);
                Object value = rs.getObject(columnName);
                Field filed = returnTypeClass.getDeclaredField(columnName);
                filed.setAccessible(true);
                filed.set(o, value);
            }
            results.add((T) o);
        }

    }

    private void setParameters(PreparedStatement preparedStatement, Object param, BoundSql boundSql) throws Exception {
        if (SimpleTypeRegistry.isSimpleType(param.getClass())) {
            //一个参数
            preparedStatement.setObject(1, param);
        } else if (param instanceof Map) {
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                String paramName=parameterMapping.getName();
                Object value=((Map) param).get(paramName);
                preparedStatement.setObject(i + 1, value);
            }
        } else {
            //POJO.....
        }

    }

    private Connection getConnection() {
      DataSource dataSource= configuration.getDataSource();
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
