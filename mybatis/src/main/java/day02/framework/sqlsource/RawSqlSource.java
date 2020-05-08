package day02.framework.sqlsource;

import day02.framework.sqlnode.MixedSqlNode;
import day02.framework.sqlsource.iface.SqlSource;

/**
 * 封装不带有${}和动态标签的整个SQL信息、比如整个sql语句只包含#{}
 */
public class RawSqlSource implements SqlSource {
    private MixedSqlNode rootSqlNode;

    /**
     * sql脚本的封装
     * 包含了所有的SqlNode节点
     * @param rootSqlNode
     */
    public RawSqlSource(MixedSqlNode rootSqlNode) {
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
