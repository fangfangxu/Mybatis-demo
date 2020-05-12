package day03;


import com.mybatis.sqlsession.DefaultSqlSession;
import com.mybatis.sqlsession.SqlSession;
import day01.utils.SimpleTypeRegistry;
import day02.po.User;
import day03.framework.config.Configuration;
import day03.framework.config.MappedStatement;
import day03.framework.sqlnode.IfSqlNode;
import day03.framework.sqlnode.MixedSqlNode;
import day03.framework.sqlnode.StaticTextSqlNode;
import day03.framework.sqlnode.TextSqlNode;
import day03.framework.sqlnode.iface.SqlNode;
import day03.framework.sqlsource.BoundSql;
import day03.framework.sqlsource.DynamicSqlSource;
import day03.framework.sqlsource.ParameterMapping;
import day03.framework.sqlsource.RawSqlSource;
import day03.framework.sqlsource.iface.SqlSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * 1、以面向对象的思维去改造mybatis手写框架
 * 2、将手写的mybatis的代码封装一个通用的框架(java工厂)给程序员使用
 */
public class MybatisV3 {


    @Test
    public void test() throws Exception {
        //需求：查询用户信息
        loadConfiguration();
        //支持简单类型
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", "方方5");
        params.put("id", 5);
        // params.put("sex", "男");
//        SqlSession sqlSession=new DefaultSqlSession(Configuration);
        List<User> users = sqlSession.selectList("test.findUserById", params);
        System.out.println(users);
    }



}
