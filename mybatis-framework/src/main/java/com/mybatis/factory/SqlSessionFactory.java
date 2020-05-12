package com.mybatis.factory;

import com.mybatis.sqlsession.SqlSession;

/**
 * 用来产生SqlSession
 */
public interface SqlSessionFactory {
    SqlSession openSession();
}
