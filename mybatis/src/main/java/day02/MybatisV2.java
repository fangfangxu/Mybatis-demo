package day02;


import day01.po.Employee;
import day01.utils.SimpleTypeRegistry;
import day02.framework.config.Configuration;
import day02.framework.config.MappedStatement;
import day02.framework.sqlnode.MixedSqlNode;
import day02.framework.sqlsource.BoundSql;
import day02.framework.sqlsource.ParameterMapping;
import day02.framework.sqlsource.iface.SqlSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * Mybatis-V2版本
 * 1、properties升级为XML
 * 2、面向过程思维优化代码
 */
public class MybatisV2 {

    //private Properties properties = new Properties();
    private Configuration configuration = new Configuration();
    private String namespace;
    /**
     *  提成全局
     */
    //如果包含${}或者动态标签那么为true
    private boolean isDynamic=false;

    @Test
    public void test() throws Exception {
        //需求：查询用户信息
        loadConfiguration();
        //支持简单类型
        List<Employee> employees = selectUserList("queryUserBySn", "孙尚香");
        System.out.println(employees);
        //支持Map
        Map<String, Object> params = new HashMap<>();
        params.put("name", "孙尚香");
        params.put("sn", "10002");
        List<Employee> employees1 = selectUserList("queryUserBySn", params);
        System.out.println(employees1);

    }

    /**
     * 加载Xml文件
     */
    private void loadConfiguration() throws Exception {
        String location = "day02/mybatis-config.xml";
        InputStream inputStream = getResourceAsStream(location);
        Document document = createDocument(inputStream);
        //按照mybatis的语义，对XML内容进行解析
        parseConfiguration(document.getRootElement());

    }


    private <T> List<T> selectUserList(String statementId, Object params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<T> results = new ArrayList<>();
        try {
            connection = getConnection();
//            String sql = properties.getProperty("db.sql." + statementId);
            //（1）先要获取MappedStatement
            MappedStatement mappedStatement = configuration.getMappedStatementById(statementId);
            //（2）再获取MappedStatement中的SqlSource
            SqlSource sqlSource = mappedStatement.getSqlSource();
            //（3）通过SqlSource的API处理，获取BoundSql
            //TODO
            /**
             * SqlSource的执行过程
             */
            BoundSql boundSql = sqlSource.getBoundSql(params);
            //（4）通过BoundSql获取到SQL语句
            String sql = boundSql.getSql();
            //获取预处理 statement
            preparedStatement = connection.prepareStatement(sql);

            //参数处理
            //设置参数
            setParameters(preparedStatement, params, boundSql);
            //向数据库发出sql执行查询，查询出结果集
            rs = preparedStatement.executeQuery();
            //处理结果集
            handleResultSet(rs, results, mappedStatement);
            return results;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
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
        return null;
    }

    private void parseConfiguration(Element rootElement) throws Exception {
        Element envirsElement = rootElement.element("environments");
        parseEnvironments(envirsElement);
        Element mappersElement = rootElement.element("mappers");
        parseMappers(mappersElement);
    }

    private void parseMappers(Element mappersElement) throws Exception {
        List<Element> mappers = mappersElement.elements("mapper");
        for (Element mapperElement : mappers) {
            parseMapper(mapperElement);
        }
    }

    /**
     * 解析Mapper
     *
     * @param mapperElement
     */
    private void parseMapper(Element mapperElement) throws Exception {
        String resource = mapperElement.attributeValue("resource");
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(resource);
        Document document = createDocument(resourceAsStream);
        //按照映射文件的语义进行解析
        parseXmlMapper(document.getRootElement());
    }

    private void parseXmlMapper(Element rootElement) throws Exception {
        namespace = rootElement.attributeValue("namespace");
        List<Element> selectElements = rootElement.elements("select");
        for (Element selectElement : selectElements) {
            parseStatementElement(selectElement);
        }

    }

    /**
     * MappedStatement
     *
     * @param selectElement
     */
    private void parseStatementElement(Element selectElement) throws Exception {
        String statementId = selectElement.attributeValue("id");
        if (statementId == null || statementId.equals("")) {
            return;
        }
        //一个CRUD标签对应一个MappedStatement对象
        //一个MappedStatement对象有一个statementId来标识，来保证唯一
        //statementId=namespace+"."+CRUD标签的id属性

        statementId = namespace + "." + statementId;
        String statementType = selectElement.attributeValue("statementType");
        statementType=statementType==null|| statementType==""?"prepared":statementType;

        String parameterTypeName = selectElement.attributeValue("parameterType");
        String resultTypeClassName = selectElement.attributeValue("resultType");

        Class<?> parameterTypeClass = resolveType(parameterTypeName);
        Class<?> resultTypeClass = resolveType(resultTypeClassName);
         //TODO
        /**
         * SqlSource的封装过程
         */
        SqlSource sqlSource = createSqlSource(selectElement);

        //TO DO 建议使用构建者模式去优化
        MappedStatement mappedStatement = new MappedStatement(statementId, sqlSource, statementType,
                parameterTypeClass, resultTypeClass);
        configuration.addMappedStatements(statementId, mappedStatement);
    }


    /**
     * SqlSource的封装过程
     * @param selectElement
     * @return
     */
    //TODO
    private SqlSource createSqlSource(Element selectElement) {
        SqlSource sqlSource=  parseScriptNode(selectElement);
        return sqlSource;
    }

    /**
     * 用来处理脚本节点
     * 处理select等标签下的SQL脚本
     * @param selectElement
     * @return
     */
    private SqlSource parseScriptNode(Element selectElement) {

       //一、解析Sql脚本信息
      MixedSqlNode rootSqlNode= parseDynamicTags(selectElement);


        //二、
        //将SqlNode封装到SqlSource对象当中
        //思考？封装到哪个SqlSource对象中
          //DynamicSqlSource
          //RawSqlSource
        SqlSource sqlSource=null;
        if(isDynamic){
            //DynamicSqlSource
            sqlSource=new DynamicSqlSource(rootSqlNode);
        }else{
            //RawSqlSource
            sqlSource=new RawSqlSource(rootSqlNode);
        }
        return sqlSource;
    }

    private Class<?> resolveType(String type) throws Exception {
        return Class.forName(type);
    }


    private void parseEnvironments(Element envirsElement) {
        String defaultId = envirsElement.attributeValue("default");
        List<Element> environments = envirsElement.elements("environment");
        for (Element element : environments) {
            if (element.attributeValue("id").equals(defaultId)) {
                parseDataSource(element);
                break;

            }
        }
    }

    /**
     * 解析数据源、放入Configuration中
     *
     * @param element
     */
    private void parseDataSource(Element element) {
        Element dataSourceElement = element.element("dataSource");
        String type = dataSourceElement.attributeValue("type");
        if (type.equals("DBCP")) {
            BasicDataSource dataSource = new BasicDataSource();
            Properties properties = parseProperties(dataSourceElement);
            dataSource.setDriverClassName(properties.getProperty("driver"));
            dataSource.setUrl(properties.getProperty("url"));
            dataSource.setUsername(properties.getProperty("username"));
            dataSource.setPassword(properties.getProperty("password"));
            configuration.setDataSource(dataSource);
        }
    }

    private Properties parseProperties(Element dataSourceElement) {
        Properties properties = new Properties();
        List<Element> propertys = dataSourceElement.elements("property");
        for (Element element : propertys) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.put(name, value);
        }
        return properties;
    }

    private Document createDocument(InputStream inputStream) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            return document;
        } catch (Exception e) {

        }
        return null;
    }


    private InputStream getResourceAsStream(String location) {
        return this.getClass().getClassLoader().getResourceAsStream(location);
    }






    /**
     * 处理结果集
     *
     * @param rs
     * @param results
     * @param mappedStatement
     * @param <T>
     */
    private <T> void handleResultSet(ResultSet rs, List<T> results, MappedStatement mappedStatement) throws
            Exception {
        Class<?> resultType = mappedStatement.getResultTypeClass();
        while (rs.next()) {
            Object result = resultType.newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Field field = resultType.getDeclaredField(columnName);
                field.setAccessible(true);
                field.set(result, rs.getObject(columnName));
            }
            results.add((T) result);
        }

    }

    /**
     * 设置参数
     *
     * @param preparedStatement
     * @param params
     */
    private void setParameters(PreparedStatement preparedStatement, Object params, BoundSql boundSql) throws
            Exception {

        if (SimpleTypeRegistry.isSimpleType(params.getClass())) {
            preparedStatement.setObject(1, params);
        } else if (params instanceof Map) {
            Map map = (Map) params;
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

            for (int i = 0; i < parameterMappings.size(); i++) {
                preparedStatement.setObject(i + 1, map.get(parameterMappings.get(i).getName()));
            }
        } else {
            // POJO .....
        }
    }

    /**
     * 通过数据源获取连接
     *
     * @return
     */
    private Connection getConnection() {
        DataSource dataSource = configuration.getDataSource();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
