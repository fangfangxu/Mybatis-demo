package com.mybatis.factory;

import com.mybatis.config.Configuration;
import com.mybatis.sqlsession.DefaultSqlSession;
import com.mybatis.sqlsession.SqlSession;

public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
