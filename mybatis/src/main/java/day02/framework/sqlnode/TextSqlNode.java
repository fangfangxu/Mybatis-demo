package day02.framework.sqlnode;

import day02.framework.sqlnode.iface.SqlNode;

import java.util.ArrayList;
import java.util.List;

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
     * @return
     */
    public boolean isDynamic(){
        if(sqlText.indexOf("${")>-1){
            return true;
        }
        return false;
    }
}
