package day02.framework.sqlsource;

import day02.framework.sqlnode.MixedSqlNode;
import day02.framework.sqlsource.iface.SqlSource;

/**
 * 封装不带有${}和动态标签的整个SQL信息、比如整个sql语句只包含#{}
 * 处理之前的SQL语句：select * from user where id=#{id}
 * 处理之后的SQL语句：select * from user where id=1-----PreparedStatement
 * 处理之后的SQL语句：select * from user where id=10-----PreparedStatement
 * 每次执行时，都需要处理一次#{}吗？----不是
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
