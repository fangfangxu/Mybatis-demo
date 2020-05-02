package com.mybatis.day01;


import com.mybatis.day01.po.Employee;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
            String name = "jdbc.properties";
            InputStream ins = this.getClass().getClassLoader().getResourceAsStream(name);
            properties.load(ins);
        } catch (Exception e) {

        }
    }


    @Test
    public void test() {
        //需求：查询用户信息
        loadProperties();
//        List<Employee> employees = selectUserList("queryUserBySn", "10001");
        List<Employee> employees = selectUserList("queryUserBySn", "刘备");
        System.out.println(employees);
    }


    //private List<Employee> selectUserList(String statementId, Object param) {
    private <T> List<T> selectUserList(String statementId, Object param) {
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
            //String sql = properties.getProperty("db.sql.queryUserBySn");
            String sql = properties.getProperty("db.sql." + statementId);
            //获取预处理 statement
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            //参数处理
//            preparedStatement.setString(1, param);
            preparedStatement.setObject(1, param);
            //向数据库发出 sql 执行查询，查询出结果集
            rs = preparedStatement.executeQuery();
            //遍历结果集
            //将每一行映射为一个User对象，每一列作为User对象的属性进行设置
            //不是User类型怎么办？将结果类型配置进.properties
            //String resultclassname=  properties.getProperty("db.sql.queryUserBySn.resultclassname");
            String resultclassname = properties.getProperty("db.sql." + statementId + ".resultclassname");

            Class<?> resultType = Class.forName(resultclassname);

            while (rs.next()) {
                //System.out.println(rs.getString("sn") + " " + rs.getString("name"));
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
