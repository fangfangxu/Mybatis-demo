package day02;


import day01.po.Employee;
import day01.utils.SimpleTypeRegistry;
import day02.framework.config.Configuration;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mybatis-V2版本
 * 1、properties升级为XML
 * 2、面向过程思维优化代码
 */
public class MybatisV2 {

    //private Properties properties = new Properties();

    private Configuration configuration = new Configuration();

    /**
     * 加载Xml文件
     */
    private void loadConfiguration() {

    }

    @Test
    public void test() {
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

        //支持Pojo
        Employee employee = new Employee();
        employee.setName("孙尚香");
        employee.setSn("10002");
        List<Employee> employees2 = selectUserList("queryUserBySn", employee);
        System.out.println(employees2);
    }

    private <T> List<T> selectUserList(String statementId, Object params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<T> results = new ArrayList<>();
        try {
            Class.forName(properties.getProperty("db.driver"));
            connection = DriverManager.getConnection(properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password"));
            String sql = properties.getProperty("db.sql." + statementId);
            preparedStatement = connection.prepareStatement(sql);
            if (SimpleTypeRegistry.isSimpleType(params.getClass())) {
                preparedStatement.setObject(1, params);
            } else if (params instanceof Map) {
                String param = properties.getProperty("db.sql." + statementId + ".params");
                String[] paramArray = param.split(",");

                for (int i = 0; i < paramArray.length; i++) {
                    preparedStatement.setObject(i + 1, ((Map) params).get(paramArray[i]));
                }
            } else {
                String param = properties.getProperty("db.sql." + statementId + ".params");
                String paramclassname = properties.getProperty("db.sql." + statementId + ".paramclassname");
                Class<?> paramClassType = Class.forName(paramclassname);

                String[] paramArray = param.split(",");
                for (int i = 0; i < paramArray.length; i++) {
                    String columnName = paramArray[i];
                    Field paramDeclaredField = paramClassType.getDeclaredField(columnName);
                    paramDeclaredField.setAccessible(true);
                    Object value = paramDeclaredField.get(params);
                    preparedStatement.setObject(i + 1, value);
                }
            }
            rs = preparedStatement.executeQuery();
            String resultclassname = properties.getProperty("db.sql." + statementId + ".resultclassname");
            Class<?> resultType = Class.forName(resultclassname);
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

}
