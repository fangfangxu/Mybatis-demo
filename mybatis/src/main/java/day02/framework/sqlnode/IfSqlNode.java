package day02.framework.sqlnode;

import day02.framework.sqlnode.iface.SqlNode;

/**
 * 封装if标签对应的sql脚本信息
 * （1）封装SQL脚本片段
 * （2）处理SQL脚本片段
 */
public class IfSqlNode implements SqlNode {
    //判断条件
    private String test;
    //if标签内的SqlNode集合信息
    private MixedSqlNode mixedSqlNode;

    public IfSqlNode(String test, MixedSqlNode mixedSqlNode) {
        this.test = test;
        this.mixedSqlNode = mixedSqlNode;
    }
}
