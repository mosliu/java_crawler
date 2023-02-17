package net.liuxuan.crawler.entity.feedsdb;

import lombok.Data;
import lombok.experimental.Accessors;
import net.liuxuan.crawler.webmagic.domain.ProcessorDomain;
import net.liuxuan.crawler.webmagic.domain.SiteDomain;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "java_crawler_tasks")
@DynamicInsert
public class JavaCrawlerTasks implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 任务名称,唯一
     */
    @Column(name = "task_name")
    private String taskName;

    /**
     * 任务类型
     */
    @Column(name = "task_type")
    private Integer taskType;

    /**
     * 基础url
     */
    @Column(name = "baseUrl")
    private String baseUrl;

    /**
     * site配置情况，json
     */
    @Column(name = "site_conf")
    private String siteConf;

    /**
     * json,解析规则
     */
    @Column(name = "rule", columnDefinition = "text")
    private String rule;

    @Column(name = "downloader")
    private String downloader;

    @Column(name = "processor")
    private String processor;
    /**
     * pipeline,逗号分隔
     */
    @Column(name = "pipelines")
    private String pipelines;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 任务说明
     */
    @Column(name = "comment")
    private String comment;


    /**
     * 是否启用
     */
    @Column(name = "is_active")
    private Boolean active;


    public SiteDomain getSitedomain() {
        return SiteDomain.fromJson(siteConf);
    }

    public ProcessorDomain getProcessorDomain() {
        return ProcessorDomain.fromJson(rule);
    }
}
