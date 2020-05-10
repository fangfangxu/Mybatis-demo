package day02.framework.sqlnode;

import day02.framework.sqlnode.iface.SqlNode;
import day02.framework.sqlnode.support.DynamicContext;

/**
 * 封装不带有${}的SQL文本信息
 * （1）封装SQL脚本片段
 * （2）处理SQL脚本片段
 */
public class StaticTextSqlNode implements SqlNode {
    private String sqlText;

    /**
     * 通过构造的方式进行注入sql文本
     */
    public StaticTextSqlNode(String sqlText) {
        this.sqlText = sqlText;
    }

    @Override
    public void apply(DynamicContext context) {
        context.appendSql(sqlText);
    }
}
