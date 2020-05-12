package com.mybatis.builder;

import com.mybatis.config.Configuration;
import com.mybatis.factory.DefaultSqlSessionFactory;
import com.mybatis.factory.SqlSessionFactory;
import com.mybatis.utils.DocumentUtils;
import org.dom4j.Document;

import java.io.InputStream;

/**
 * 作用：解析配置文件，封装Configuration，创建SqlSessionFactory
 * 构建者模式
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream inputStream) throws Exception {
        XMLConfigBuilder configBuilder=new XMLConfigBuilder();
        Document document= DocumentUtils.createDocument(inputStream);
        Configuration configuration=configBuilder.parse(document.getRootElement());
        return build(configuration);
    }

    private SqlSessionFactory build(Configuration configuration){
        return new DefaultSqlSessionFactory(configuration);
    }
}
