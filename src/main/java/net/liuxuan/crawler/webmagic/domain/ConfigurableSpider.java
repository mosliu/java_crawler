package net.liuxuan.crawler.webmagic.domain;

import lombok.Data;
import net.liuxuan.crawler.entity.ListPatternModel;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

/**
 * 2019/9/10 14:58
 * yechangjun
 */
@Data
public class ConfigurableSpider extends CommonDomain {

    private String name;

    private String tableName;

    //列表页正则表达式"
    private String listRegex;

    //入口页
    private String entryUrl;

    //正文页xpath")
    private String contentXpath;

    //列表页字段规则json字符串")
    private String fieldsJson;

    //正文页规则json字符串")
    private String contentFieldsJson;

    //爬虫配置项
    //页面爬取是否是用动态抓取 默认0启用 1.启用")
    private Integer isDynamic;

    //线程数")
    private Integer threadNum;

    //每个页面处理完后的睡眠时间 单位秒")
    private Integer sleepTime;

    //页面下载失败重试次数")
    private Integer retryTimes;

    //重试睡眠时间 单位秒")
    private Integer retrySleepTime;

    //页面爬取失败后放回队列的次数")
    private Integer cycleRetryTimes;

    //下载页面超时时间 单位秒")
    private Integer timeOut;

    //代理配置
    //代理id")
    private String proxyChannelId;

    private LinkedHashMap<String/*字段名*/, String/*xpath*/> fields;

    private LinkedHashMap<String/*字段名*/, String/*xpath*/> contentFields;

    public static ConfigurableSpider valueOf(ListPatternModel model) {
        ConfigurableSpider spider = new ConfigurableSpider();

        spider.setName(model.getName());
        spider.setTableName(model.getTableName());
        spider.setListRegex(model.getListRegex());
        spider.setEntryUrl(model.getEntryUrl());
        spider.setFieldsJson(model.getFieldsJson());
        spider.setContentXpath(model.getContentXpath());
        spider.setContentFieldsJson(model.getContentFieldsJson());

        spider.setIsDynamic(model.getIsDynamic() == null ? 0 : model.getIsDynamic());
        spider.setThreadNum(4);
        spider.setSleepTime(0);
        spider.setRetryTimes(0);
        spider.setRetrySleepTime(1);
        spider.setCycleRetryTimes(0);
        spider.setTimeOut(5);

        spider.setCreateTime(LocalDateTime.now());
        spider.setModifyTime(LocalDateTime.now());

        return spider;
    }
}
