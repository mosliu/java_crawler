package net.liuxuan.crawler.entity;

import lombok.Data;

/**
 * 2019/8/28 14:56
 * yechangjun
 * 列表模式
 */
@Data
public class ListPatternModel {
    //爬虫名", required = true)
    private String name;

    //列表页正则表达式", required = true)
    private String listRegex;

    //入口页", required = true)
    private String entryUrl;

    //存储的表名", required = true)
    private String tableName;

    //入口页字段规则json字符串")
    private String fieldsJson;

    //正文页xpath")
    private String contentXpath;

    //正文页规则json字符串")
    private String contentFieldsJson;

    //页面爬取是否是用动态抓取 默认0启用 1.启用")
    private Integer isDynamic;
}
