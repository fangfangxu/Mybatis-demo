package com.mybatis.builder;

import com.mybatis.config.Configuration;
import com.mybatis.config.MappedStatement;
import com.mybatis.sqlnode.IfSqlNode;
import com.mybatis.sqlnode.MixedSqlNode;
import com.mybatis.sqlnode.StaticTextSqlNode;
import com.mybatis.sqlnode.TextSqlNode;
import com.mybatis.sqlnode.iface.SqlNode;
import com.mybatis.sqlsource.DynamicSqlSource;
import com.mybatis.sqlsource.RawSqlSource;
import com.mybatis.sqlsource.iface.SqlSource;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析映射文件
 */
public class XMLMapperBuilder {
    private Configuration configuration;
    private String namespace;
    /**
     * 提成全局
     */
    //如果包含${}或者动态标签那么为true
    private boolean isDynamic = false;
    public XMLMapperBuilder(Configuration configuration){
        this.configuration=configuration;
    }

    public void parse(Element element) throws Exception {
        namespace = element.attributeValue("namespace");
        List<Element> selectElements = element.elements("select");
        for (Element selectElement : selectElements) {
            /**
             * 希望专人解析
             */
//            parseStatementElement(selectElement);
        }
    }


    /**
     * MappedStatement
     *
     * @param selectElement
     */
    private void parseStatementElement(Element selectElement) throws Exception {
        String statementId = selectElement.attributeValue("id");
        if (statementId == null || statementId.equals("")) {
            return;
        }
        //一个CRUD标签对应一个MappedStatement对象
        //一个MappedStatement对象有一个statementId来标识，来保证唯一
        //statementId=namespace+"."+CRUD标签的id属性

        statementId = namespace + "." + statementId;
        String statementType = selectElement.attributeValue("statementType");
        statementType = statementType == null || statementType == "" ? "prepared" : statementType;

        String parameterTypeName = selectElement.attributeValue("parameterType");
        String resultTypeClassName = selectElement.attributeValue("resultType");

        Class<?> parameterTypeClass = resolveType(parameterTypeName);
        Class<?> resultTypeClass = resolveType(resultTypeClassName);

        /**
         * SqlSource的封装过程
         */
        SqlSource sqlSource = createSqlSource(selectElement);

        //TODO 建议使用构建者模式去优化
        MappedStatement mappedStatement = new MappedStatement(statementId, sqlSource, statementType,
                parameterTypeClass, resultTypeClass);
        configuration.addMappedStatements(statementId, mappedStatement);
    }
    private Class<?> resolveType(String type) throws Exception {
        return Class.forName(type);
    }


    /**
     * SqlSource的封装过程
     *
     * @param selectElement
     * @return
     */
    //TODO
    private SqlSource createSqlSource(Element selectElement) {
        SqlSource sqlSource = parseScriptNode(selectElement);
        return sqlSource;
    }


    /**
     * 用来处理脚本节点
     * 处理select等标签下的SQL脚本
     *
     * @param selectElement
     * @return
     */
    private SqlSource parseScriptNode(Element selectElement) {

        //一、解析Sql脚本信息
        MixedSqlNode rootSqlNode = parseDynamicTags(selectElement);
        //二、
        //将SqlNode封装到SqlSource对象当中
        //思考？封装到哪个SqlSource对象中
        //DynamicSqlSource
        //RawSqlSource
        SqlSource sqlSource = null;
        if (isDynamic) {
            //DynamicSqlSource
            sqlSource = new DynamicSqlSource(rootSqlNode);
        } else {
            //RawSqlSource
            sqlSource = new RawSqlSource(rootSqlNode);
        }
        return sqlSource;
    }
    /**
     * 解析Sql脚本信息
     *
     * @param selectElement
     * @return
     */
    private MixedSqlNode parseDynamicTags(Element selectElement) {
        List<SqlNode> sqlNodes = new ArrayList<SqlNode>();

        //获取select标签内的子节点的总数
        int nodeCount = selectElement.nodeCount();
        //遍历所有子节点
        for (int i = 0; i < nodeCount; i++) {
            Node node = selectElement.node(i);
            //判断子节点是文本节点还是元素节点
            if (node instanceof Text) {
                //获取文本信息
                String sqlText = node.getText();
                if (sqlText == null || "".equals(sqlText)) {
                    continue;
                }
                //将sql文本封装到TextSqlNode
                TextSqlNode textSqlNode = new TextSqlNode(sqlText);
                //判断文本中是否有${},这个文本是TextSqlNode给我提供的，
                //那个是否包含${}，给我提供文本的人告诉我
                //谁封装sqlText，谁对sqlText最了解，判断是否有${}
                if (textSqlNode.isDynamic()) {
                    //如果包含${}
                    isDynamic = true;
                    sqlNodes.add(textSqlNode);
                } else {
                    //如果不包含${}
                    sqlNodes.add(new StaticTextSqlNode(sqlText));
                }

            } else if (node instanceof Element) {
                //元素 :本身就是一个动态标签
                Element element = (Element) node;
                String elementName = element.getName();
                if (elementName.equals("if")) {
                    String test = element.attributeValue("test");
                    /**
                     * 递归封装SqlNode数据
                     */
                    MixedSqlNode mixedSqlNode = parseDynamicTags(element);
                    //封装IfSqlNode数据
                    IfSqlNode ifSqlNode = new IfSqlNode(test, mixedSqlNode);
                    sqlNodes.add(ifSqlNode);

                } else if (elementName.equals("where")) {
                    //....
                }//....
                isDynamic = true;
            }

        }
        return new MixedSqlNode(sqlNodes);
    }
}
