package net.liuxuan.crawler.entity.feedsdb;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 热点头条-带正文摘要部分
 */
@Data
@Entity
@Accessors(chain = true)
@Table(name = "java_crawler_hot_point_content")
public class JavaCrawlerHotPointContent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * "baidu"百度风云榜 "weixin_reci"微信热搜 "weibo" 实时热点  "weibo_realtimehot" 热搜榜 "weibo_socialevent" 新时代 "qq"
     */
    @Column(name = "type", columnDefinition = "char")
    private String type;

    /**
     * 热搜组 json
     */
    @Column(name = "group_data", columnDefinition = "text")
    private String groupData;

    @Column(name = "url")
    private String url;

    @Column(name = "url_md5")
    private String urlMd5;

    /**
     * 一组标识，整个榜单采集时间
     */
    @Column(name = "gid")
    private String gid;

    /**
     * 0
     */
    @Column(name = "num")
    private Integer num;

    /**
     * 添加时间
     */
    @Column(name = "add_time", nullable = false)
    private LocalDateTime addTime;

    /**
     * 导语或摘要
     */
    @Column(name = "`desc`")
    private String desc;


    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "title")
    private String title;

    /**
     * 热点值
     */
    @Column(name = "hot_num")
    private String hotNum;

    /**
     * 排行
     */
    @Column(name = "sort_num")
    private String sortNum;

    /**
     * 抓取状态码
     */
    @Column(name = "status", columnDefinition = "tinyint")
    private Integer status;

    @Column(name = "city_name")
    private String cityName;

}
