package day02.framework.sqlsource;

import day02.framework.sqlsource.iface.SqlSource;

/**
 * 封装带有${}或者动态标签的整个SQL信息
 */
public class DynamicSqlSource implements SqlSource {
    @Override
    public BoundSql getBoundSql(Object param) {
        return null;
    }
}
