package com.mybatis.sqlsession;

import com.mybatis.config.Configuration;

import java.util.List;

public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration){
        this.configuration=configuration;
    }

    @Override
    public <T> List<T> selectList(String statementId, Object params) {
        return null;
    }

    @Override
    public <T> T selectOne(String statementId, Object params) {
        return null;
    }
}
