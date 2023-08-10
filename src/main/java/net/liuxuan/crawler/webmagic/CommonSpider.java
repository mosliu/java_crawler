package net.liuxuan.crawler.webmagic;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.webmagic.domain.SpiderDomain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.webmagic 包下xxxx工具
 * @date 2023/2/8
 **/
@Slf4j
@Getter
@Setter
public class CommonSpider extends Spider {
    /**
     * create a spider with pageProcessor.
     *
     * @param pageProcessor pageProcessor
     */
    public CommonSpider(PageProcessor pageProcessor) {
        super(pageProcessor);
    }

    private SpiderDomain SPIDER_INFO;

    public CommonSpider(PageProcessor pageProcessor, SpiderDomain spiderInfo) {
        super(pageProcessor);
        this.SPIDER_INFO = spiderInfo;
    }

    @Override
    protected void onSuccess(Request request) {
        super.onSuccess(request);
        boolean reachMax = false, exceedRatio = false;
//        if (
//                (
//                        //已抓取数量大于最大抓取页数,退出
//                        (reachMax = (SPIDER_INFO.getMaxPageGather() > 0 && task.getCount() >= SPIDER_INFO.getMaxPageGather()))
//                                ||
//                                //如果抓取页面超过最大抓取数量ratio倍的时候,仍未达到最大抓取数量,爬虫也退出
//                                (exceedRatio = (this.getPageCount() > SPIDER_INFO.getMaxPageGather() * staticValue.getCommonsWebpageCrawlRatio() && SPIDER_INFO.getMaxPageGather() > 0))
//                )
//                        && this.getStatus() == Status.Running) {
//            log.info("爬虫ID{}已处理{}个页面,有效页面{}个,最大抓取页数{},reachMax={},exceedRatio={},退出.", this.getUUID(), this.getPageCount(), task.getCount(), SPIDER_INFO.getMaxPageGather(), reachMax, exceedRatio);
//            task.setDescription("爬虫ID%s已处理%s个页面,有效页面%s个,达到最大抓取页数%s,reachMax=%s,exceedRatio=%s,退出.", this.getUUID(), this.getPageCount(), task.getCount(), SPIDER_INFO.getMaxPageGather(), reachMax, exceedRatio);
//            this.stop();
//        }
    }

    @Override
    protected void onError(Request request) {
        super.onError(request);
//        Task task = taskManager.getTaskById(this.getUUID());
//        task.setDescription("处理网页%s时发生错误,%s", request.getUrl(), request.getExtras());
    }

    @Override
    public void close() {
        super.close();
//        Task task = taskManager.getTaskById(this.getUUID());
//        if (task != null) {
//            //清除抓取列表缓存
//            commonWebpagePipeline.deleteUrls(task.getTaskId());
//            taskManager.stopTask(task);
//        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CommonSpider CommonSpider = (CommonSpider) o;

        return new EqualsBuilder()
                .append(this.getUUID(), CommonSpider.getUUID())
                .isEquals();
    }

//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder(17, 37)
//                .append(this.getUUID())
//                .toHashCode();
//    }

    public Downloader getDownloader() {
        return downloader;
    }

}
