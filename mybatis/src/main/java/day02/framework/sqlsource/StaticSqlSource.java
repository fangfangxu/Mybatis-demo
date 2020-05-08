package day02.framework.sqlsource;

import day02.framework.sqlsource.iface.SqlSource;

/**
 * 只用作处理过程
 * （1）只封装处理之后的SQL语句（JDBC可直接执行的SQL语句）
 * （1）相对应的参数信息
 */
public class StaticSqlSource implements SqlSource {


    /**
     * sql脚本的处理
     * @param param
     * @return
     */
    @Override
    public BoundSql getBoundSql(Object param) {
        return null;
    }
}
