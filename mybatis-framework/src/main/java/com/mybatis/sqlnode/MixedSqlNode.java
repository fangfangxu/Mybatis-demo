package com.mybatis.sqlnode;



import com.mybatis.sqlnode.iface.SqlNode;
import com.mybatis.sqlnode.support.DynamicContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装平级的sqlNode集合信息
 * （1）封装SQL脚本片段
 * （2）处理SQL脚本片段
 */
public class MixedSqlNode implements SqlNode {
    private List<SqlNode> sqlNodes = new ArrayList<>();

    /**
     * 通过构造的方式进行注入
     */
    public MixedSqlNode(List<SqlNode> sqlNodes) {
        this.sqlNodes = sqlNodes;
    }


    @Override
    public void apply(DynamicContext context) {
        for(SqlNode sqlNode:sqlNodes){
            sqlNode.apply(context);
        }
    }
}
