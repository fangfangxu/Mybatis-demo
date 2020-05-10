package day02.framework.sqlnode.iface;

import day02.framework.sqlnode.support.DynamicContext;

/**
 * 提供对封装的SqlNode信息进行解析处理
 * （1）提供封装SQL脚本片段的能力
 * （2）提供处理SQL脚本片段的能力
 */
public interface SqlNode {
    //    void applay(StringBuffer sb,Object params);

    /**
     * 节点处理
     * @param context 节点处理时需要的上下文数据
     */
    void apply(DynamicContext context);
}
