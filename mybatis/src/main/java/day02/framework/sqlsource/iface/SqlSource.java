package day02.framework.sqlsource.iface;

import day02.framework.sqlsource.BoundSql;

/**
 * 提供对select/update/delete/insert标签内的SQL语句的处理
 * 需经过getBoundSql处理才可以让JDBC直接执行
 */
public interface SqlSource {
    /**
     * 解析SqlSource实现类存储的SQL信息
     * #{}：PreparedStatement处理方式：解析过程中不需要参数，执行才需要
     *       Select * from employee where sn=? and name=?
     * ${}：Statement处理方式 :解析过程就需要参数,否则得不到JDBC可执行语句
     *   只有拼接上实参才算是JDBC可执行SQL，所以 getBoundSql(Object param)
     *        Select * from employee where sn="sn的值" and name="name的值"
     * @param param
     * @return
     */
    BoundSql getBoundSql(Object param);
}
