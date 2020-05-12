package com.mybatis.sqlsource;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储了select/update/delete/insert标签内的sql语句最终被处理之后的SQL
 * 存储处理之后的JDBC可以直接执行的SQL语句
 */
public class BoundSql {
    //jdbc可以执行的SQL语句
    //select * from employee where sn=? and name=?
    private String sql;
    //占位符?对应的参数信息（参数名称、参数类型）
    private List<ParameterMapping> parameterMappings=new ArrayList<>();

    public BoundSql(String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    /**
     * 处理为Add
     * @param parameterMapping
     */
    public void addParameterMappings(ParameterMapping parameterMapping) {
        this.parameterMappings.add(parameterMapping);
    }
}
