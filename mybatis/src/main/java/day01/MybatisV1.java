package day01;




import day01.po.Employee;
import day01.utils.SimpleTypeRegistry;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class MybatisV1 {
    /**
     * Properties文件存储的集合对象
     */
    private Properties properties = new Properties();

    /**
     * 加载properties文件
     */
    private void loadProperties() {
        try {
            String name = "day01/jdbc.properties";
            InputStream ins = this.getClass().getClassLoader().getResourceAsStream(name);
            properties.load(ins);
        } catch (Exception e) {

        }
    }


//    @Test
//    public void test() {
//        //需求：查询用户信息
//        loadProperties();
////        List<Employee> employees = selectUserList("queryUserBySn", "10001");
//        List<Employee> employees = selectUserList("queryUserBySn", "刘备");
//        System.out.println(employees);
//    }


//    /**
//     * 版本V1.1
//     * 硬编码问题解决、返回结果支持通用
//     * @param statementId
//     * @param param
//     * @param <T>
//     * @return
//     */
//    private <T> List<T> selectUserList(String statementId, Object param) {
//        Connection connection = null;
//        PreparedStatement preparedStatement = null;
//        ResultSet rs = null;
//
//        List<T> results = new ArrayList<>();
//        try {
//            //加载数据库驱动
//            Class.forName(properties.getProperty("db.driver"));
//            //通过驱动管理器获取数据库连接Connection
//            connection = DriverManager.getConnection(properties.getProperty("db.url"),
//                    properties.getProperty("db.username"),
//                    properties.getProperty("db.password"));
//            //定义sql语句
//            //String sql = properties.getProperty("db.sql.queryUserBySn");
//            String sql = properties.getProperty("db.sql." + statementId);
//            //获取预处理 statement
//            preparedStatement = connection.prepareStatement(sql);
//            //设置参数
//            //参数处理
////            preparedStatement.setString(1, param);
//            preparedStatement.setObject(1, param);
//            //向数据库发出 sql 执行查询，查询出结果集
//            rs = preparedStatement.executeQuery();
//            //遍历结果集
//            //将每一行映射为一个User对象，每一列作为User对象的属性进行设置
//            //不是User类型怎么办？将结果类型配置进.properties
//            //String resultclassname=  properties.getProperty("db.sql.queryUserBySn.resultclassname");
//            String resultclassname = properties.getProperty("db.sql." + statementId + ".resultclassname");
//
//            Class<?> resultType = Class.forName(resultclassname);
//
//            while (rs.next()) {
//                //System.out.println(rs.getString("sn") + " " + rs.getString("name"));
//                //结果处理
//                Object result = resultType.newInstance();
//                //给字段赋值
//                ResultSetMetaData metaData = rs.getMetaData();
//                int columnCount = metaData.getColumnCount();
//                for (int i = 1; i <= columnCount; i++) {
//                    //获取列名称
//                    String columnName = metaData.getColumnName(i);
//                    Field field = resultType.getDeclaredField(columnName);
//                    field.setAccessible(true);
//                    field.set(result, rs.getObject(columnName));
//                }
//                results.add((T) result);
//            }
//
//            return results;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            //释放资源
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if (preparedStatement != null) {
//                try {
//                    preparedStatement.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if (rs != null) {
//                try {
//                    rs.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }


    @Test
    public void test() {
        //需求：查询用户信息
        loadProperties();
        //支持简单类型
        List<Employee> employees = selectUserList("queryUserBySn", "孙尚香");
        System.out.println(employees);
        //支持Map
        Map<String,Object> params=new HashMap<>();
        params.put("name","孙尚香");
        params.put("sn","10002");
        List<Employee> employees1 = selectUserList("queryUserBySn", params);
        System.out.println(employees1);

        //支持Pojo
        Employee employee=new Employee();
        employee.setName("孙尚香");
        employee.setSn("10002");
        List<Employee> employees2 = selectUserList("queryUserBySn", employee);
        System.out.println(employees2);
    }

    /**
     * 版本V1.2  单一入参处理为通用支持多个
     *
     * @param statementId
     * @param params
     * @param <T>
     * @return
     */
    private <T> List<T> selectUserList(String statementId, Object params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<T> results = new ArrayList<>();
        try {
            //加载数据库驱动
            Class.forName(properties.getProperty("db.driver"));
            //通过驱动管理器获取数据库连接Connection
            connection = DriverManager.getConnection(properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password"));
            //定义sql语句
            String sql = properties.getProperty("db.sql." + statementId);
            //获取预处理 statement
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            //参数处理
            //判断参数类型是Map还是简单类型
            if(SimpleTypeRegistry.isSimpleType(params.getClass())){
                //简单类型-说明入参就是一个基本类型
                preparedStatement.setObject(1, params);
            }else if(params instanceof Map){
                //Map-手动
              String param = properties.getProperty("db.sql." + statementId + ".params");
              String[] paramArray = param.split(",");

               for(int i=0;i<paramArray.length;i++){
                   preparedStatement.setObject(i+1, ((Map) params).get(paramArray[i]));
               }
            }else{
                //.....暂不做处理 eg:Pojo
                String param=properties.getProperty("db.sql."+statementId+".params");
                //获取Pojo类型
                String paramclassname= properties.getProperty("db.sql."+statementId+".paramclassname");
                Class<?> paramClassType = Class.forName(paramclassname);

                String[] paramArray=param.split(",");
                for(int i=0;i<paramArray.length;i++){
                    String columnName=paramArray[i];
                    Field paramDeclaredField = paramClassType.getDeclaredField(columnName);
                    paramDeclaredField.setAccessible(true);
                    Object value=paramDeclaredField.get(params);
                    preparedStatement.setObject(i+1, value);
                }
            }

            //向数据库发出 sql 执行查询，查询出结果集
            rs = preparedStatement.executeQuery();
            //遍历结果集
            String resultclassname = properties.getProperty("db.sql." + statementId + ".resultclassname");
            Class<?> resultType = Class.forName(resultclassname);
            while (rs.next()) {
                //结果处理
                Object result = resultType.newInstance();
                //给字段赋值
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    //获取列名称
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
            //释放资源
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
