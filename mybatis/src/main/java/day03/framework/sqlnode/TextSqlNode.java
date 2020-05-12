package day03.framework.sqlnode;

import day03.framework.sqlnode.iface.SqlNode;
import day03.framework.sqlnode.support.DynamicContext;
import day03.framework.utils.GenericTokenParser;
import day03.framework.utils.OgnlUtils;
import day03.framework.utils.SimpleTypeRegistry;
import day03.framework.utils.TokenHandler;

/**
 * 封装带有${}的SQL文本信息
 * （1）封装SQL脚本片段
 * （2）处理SQL脚本片段
 */
public class TextSqlNode implements SqlNode {
    private String sqlText;

    /**
     * 通过构造的方式进行注入sql文本
     */
    public TextSqlNode(String sqlText) {
        this.sqlText = sqlText;
    }

    /**
     * 谁封装sqlText，谁对sqlText最了解，判断是否有${}
     *
     * @return
     */
    public boolean isDynamic() {
        if (sqlText.indexOf("${") > -1) {
            return true;
        }
        return false;
    }


    @Override
    public void apply(DynamicContext context) {
        //主要用来处理${}中的参数名称，从入参对象中获取对应的参数值
        TokenHandler handler = new BindingTokenHandler(context);
        //通用的分词解析器：根据${和}去截取字符串，最终匹配到的${}中的内容，交给TokenHandler去处理
        GenericTokenParser tokenParser = new GenericTokenParser("${", "}", handler);
        //执行解析过程，返回值是处理完${}之后的值
        String sql = tokenParser.parse(sqlText);
        context.appendSql(sql);
    }

    class BindingTokenHandler implements TokenHandler {
        private DynamicContext context;

        public BindingTokenHandler(DynamicContext context) {
            this.context = context;
        }

        @Override
        public String handleToken(String content) {
            Object parameterObject = context.getBindings().get("_parameter");
            if (parameterObject == null) {
                return "";
            } else if (SimpleTypeRegistry.isSimpleType(parameterObject.getClass())) {
                return parameterObject.toString();
            }

            Object value = OgnlUtils.getValue(content, parameterObject);
            return value == null ? "" : value.toString();
        }
    }
}
