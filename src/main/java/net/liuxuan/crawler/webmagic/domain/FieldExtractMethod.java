package net.liuxuan.crawler.webmagic.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 提取字段的方法
 * @date 2023/2/7
 **/
@Data
@Accessors(chain = true)
public class FieldExtractMethod {
    public static final int METHOD_XPATH = 0;
    public static final int METHOD_CSS = 1;
    public static final int METHOD_REGEX = 2;
    public static final int METHOD_SMART_CONTENT = 3;

    public static final int POS_NONE = 0;
    public static final int POS_CONTENT = 1;
    public static final int POS_ATTR = 2;
    public static final int FINAL_RESULT_TYPE_GET = 0;
    public static final int FINAL_RESULT_TYPE_ALL = 1;

    String fieldName;
    /**
     * 0: xpath, 1: css, 2: regex,3:smartContent
     */
    int method;
    /**
     * 字段位置，0: 不处理 1：内部问文本，2 属性attr
     */
    int pos;
    /**
     * 属性名，只有css使用
     */
    String attrName;
    String expression;

    /**
     * 最终获取结果的方式：0：get() 1：all() 2：smartContent()
     */
    int finalResultType = 0;

    FieldExtractMethod child = null;


    public boolean hasChild() {
        return child != null;
    }

    public static FieldExtractMethod n(){
        return new FieldExtractMethod();
    }

}
