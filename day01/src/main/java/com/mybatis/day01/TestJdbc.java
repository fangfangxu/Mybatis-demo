package com.mybatis.day01;

import org.junit.jupiter.api.Test;

import java.sql.*;

public class TestJdbc {

    @Test
    public void test() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            //加载数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //通过驱动管理器获取数据库连接Connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/oa?characterEncoding=utf-8&serverTimezone=UTC", "root", "123456");
            //定义sql语句
            String sql = "select * from employee where sn=?";
            //获取statement
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            preparedStatement.setString(1,"10001");
            //向数据库发起执行
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("sn") + " " + rs.getString("name"));
            }
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


    }
}
