package day02.framework.sqlsource;

import day02.framework.sqlnode.MixedSqlNode;
import day02.framework.sqlnode.support.DynamicContext;
import day02.framework.sqlsource.iface.SqlSource;
import day02.framework.utils.GenericTokenParser;
import day02.framework.utils.ParameterMappingTokenHandler;


/**
 * 封装带有${}或者动态标签的整个SQL信息
 * 处理之后的SQL语句：SELECT * FROM user WHERE id = 1 ---- Statement 处理之后的SQL语句：SELECT *
 * FROM user WHERE id = 10 ---- Statement 是否需要每次执行的时候，都要处理一次${}？------是的
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
        //SqlSource封装SqlNode的上下文DynamicContext
        DynamicContext context=new DynamicContext(param);
        //在此处解析SqlNode集合,合并成一条SQL语句（可能还带有#{}、但是已经处理了${}和动态标签）
        rootSqlNode.apply(context);
        System.out.println("解析#{}之前的SQL语句："+context.getSql());
        //针对#{}进行处理
        //主要用来处理#{}替换为？，将入参对象名称封装进入ParameterMapping中
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler();
        //通用的分词解析器：根据#{和}去截取字符串，最终匹配到的#{}中的内容，交给TokenHandler去处理
        GenericTokenParser tokenParser = new GenericTokenParser("#{", "}", handler);
        //执行解析过程，返回值是处理完#{}之后的值
        String sql = tokenParser.parse(context.getSql());
        System.out.println("解析#{}之后的SQL语句："+sql);
        return new BoundSql(sql,handler.getParameterMappings());
    }
}
