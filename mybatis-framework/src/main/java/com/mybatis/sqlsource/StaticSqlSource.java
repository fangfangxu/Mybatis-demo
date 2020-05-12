package com.mybatis.sqlsource;


import com.mybatis.sqlsource.iface.SqlSource;

import java.util.List;

/**
 * 只用作处理过程
 * （1）只封装处理之后的SQL语句（JDBC可直接执行的SQL语句）
 * （1）相对应的参数信息
 */
public class StaticSqlSource implements SqlSource {

    private String sql;
    private List<ParameterMapping> parameterMappings;

    public StaticSqlSource(String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }

    /**
     * sql脚本的处理
     *
     * @param param
     * @return
     */
    @Override
    public BoundSql getBoundSql(Object param) {
        return new BoundSql(sql,parameterMappings);
    }
}
