package day02.framework.sqlnode;

import day02.framework.sqlnode.iface.SqlNode;
import day02.framework.sqlnode.support.DynamicContext;
import day02.framework.utils.OgnlUtils;

import java.util.Map;

/**
 * 封装if标签对应的sql脚本信息
 * （1）封装SQL脚本片段
 * （2）处理SQL脚本片段
 */
public class IfSqlNode implements SqlNode {
    //判断条件(OGNL表达式)
    private String test;
    //if标签内的SqlNode集合信息
    private MixedSqlNode mixedSqlNode;

    public IfSqlNode(String test, MixedSqlNode mixedSqlNode) {
        this.test = test;
        this.mixedSqlNode = mixedSqlNode;
    }


    @Override
    public void apply(DynamicContext context) {
        //获取入参对象
        Object parameterObject = context.getBindings().get("_parameter");
        //使用OGNL工具类去判断表达式
        boolean evaluateBoolean = OgnlUtils.evaluateBoolean(test, parameterObject);
        if(evaluateBoolean){
            mixedSqlNode.apply(context);
        }
    }
}
