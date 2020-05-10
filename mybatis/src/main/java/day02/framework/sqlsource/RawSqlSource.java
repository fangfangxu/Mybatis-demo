package day02.framework.sqlsource;

import day02.framework.sqlnode.MixedSqlNode;
import day02.framework.sqlnode.TextSqlNode;
import day02.framework.sqlnode.support.DynamicContext;
import day02.framework.sqlsource.iface.SqlSource;
import day02.framework.utils.GenericTokenParser;
import day02.framework.utils.ParameterMappingTokenHandler;
import day02.framework.utils.TokenHandler;

/**
 * 封装不带有${}和动态标签的整个SQL信息 处理之前的SQL语句：SELECT * FROM user WHERE id = #{id}
 * 处理之后的SQL语句：SELECT * FROM user WHERE id = ? ---- PreparedStatement
 * 是否需要每次执行的时候，都要处理一次#{}？------不是的
 */
public class RawSqlSource implements SqlSource {
   private StaticSqlSource sqlSource;

    /**
     * sql脚本的封装
     * 包含了所有的SqlNode节点
     * @param rootSqlNode
     */
    public RawSqlSource(MixedSqlNode rootSqlNode) {
        //在此处解析SqlNode集合
        DynamicContext context=new DynamicContext(null);

        rootSqlNode.apply(context);
        //针对#{}进行处理
        //主要用来处理#{}替换为？，将入参对象名称封装进入ParameterMapping中
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler();
        //通用的分词解析器：根据#{和}去截取字符串，最终匹配到的#{}中的内容，交给TokenHandler去处理
        GenericTokenParser tokenParser = new GenericTokenParser("#{", "}", handler);
        //执行解析过程，返回值是处理完#{}之后的值
        String sql = tokenParser.parse(context.getSql());
        sqlSource=new StaticSqlSource(sql,handler.getParameterMappings());
    }

    /**
     * sql脚本的处理
     * @param param
     * @return
     */
    @Override
    public BoundSql getBoundSql(Object param) {
        return sqlSource.getBoundSql(param);
    }
}
