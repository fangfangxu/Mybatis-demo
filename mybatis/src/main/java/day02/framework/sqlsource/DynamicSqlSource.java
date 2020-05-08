package day02.framework.sqlsource;

import day02.framework.sqlnode.MixedSqlNode;
import day02.framework.sqlsource.iface.SqlSource;

/**
 * 封装带有${}或者动态标签的整个SQL信息
 * SQL语句：select * from user where id=${id}
 * 每次执行时，都需要处理${}
 */
public class DynamicSqlSource implements SqlSource {
    private MixedSqlNode rootSqlNode;

    /**
     * sql脚本的封装
     * 包含了所有的SqlNode节点
     * @param rootSqlNode
     */
    public DynamicSqlSource(MixedSqlNode rootSqlNode) {
        this.rootSqlNode = rootSqlNode;
    }

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
