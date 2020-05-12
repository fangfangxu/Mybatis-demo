package com.mybatis.sqlsession;

import java.util.List;

public class DefaultSqlSession implements SqlSession {
    public <T> List<T> selectList(String statementId, Object params) {
        return null;
    }

    public <T> T selectOne(String statementId, Object params) {
        return null;
    }
}
