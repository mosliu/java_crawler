package net.liuxuan.crawler.webmagic.pipeline;

import com.hankcs.hanlp.HanLP;
import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.CrawlerDataHolder;
import net.liuxuan.crawler.entity.feedsdb.JavaCrawlerHotPointContent;
import net.liuxuan.crawler.service.DataPersistentService;
import net.liuxuan.crawler.service.JavaCrawlerHotPointContentService;
import net.liuxuan.crawler.utils.EncryptUtil;
import net.liuxuan.crawler.webmagic.CommonSpider;
import net.liuxuan.crawler.webmagic.domain.SpiderDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.webmagic.pipeline 包下xxxx工具
 * @date 2023/2/9
 **/
@Component
@Slf4j
public class HotPointContentDbPipeLine implements Pipeline {

    @Autowired
    JavaCrawlerHotPointContentService javaCrawlerHotPointContentService;
    @Autowired
    DataPersistentService dataPersistentService;

    @Override
    public void process(ResultItems resultItems, Task task) {

        ConcurrentHashMap<String, Spider> spiderMap = CrawlerDataHolder.getInstance().getSpiderMap();
        CommonSpider spider = null;
        if (task != null && task instanceof CommonSpider) {
            SpiderDomain spiderInfo = ((CommonSpider) task).getSPIDER_INFO();
            if (spiderInfo != null) {
                spider = (CommonSpider) spiderMap.get(spiderInfo.getTaskName());
            }
        }
        if (spider == null) {
            for (Spider value : spiderMap.values()) {
                if (value != null && task != null) {
                    if (value == task) {
//                        log.info("task is 11111111");
                        spider = (CommonSpider) value;
                    }
                }
            }
        }
        Map<String, Object> extraParams = resultItems.getRequest().getExtras();
        Map<String, Object> all = resultItems.getAll();
        if (all == null) {
            all = new HashMap<>();
        }
        if (extraParams != null) {
            extraParams.putAll(all);
        } else {
            extraParams = all;
        }
        JavaCrawlerHotPointContent content = new JavaCrawlerHotPointContent();
        String url = resultItems.getRequest().getUrl();
        String urlMd5 = EncryptUtil.MD5(url);
        content.setUrlMd5(urlMd5);
        content.setStatus(0);
        List<JavaCrawlerHotPointContent> all1 = javaCrawlerHotPointContentService.findAll(content);
        if (all1 != null && all1.size() > 0) {
            Map<String, Object> finalExtraParams = extraParams;
            all1.forEach(c -> {
                        c.setStatus(1);
                        Object s = get(finalExtraParams, "content");
                        String contentStr = null;
                        if (s instanceof String) {
                            contentStr = (String) s;
                        }else if (s instanceof List){
                            List<String> strings = (List<String>) s;
                            contentStr = String.join(",", strings);
                        }
                        if (isBlank(contentStr)) {
                            contentStr = get(finalExtraParams, "title");
                        }
                        if (isBlank(contentStr)) {
                            contentStr = c.getTitle();
                        }
                        c.setContent(contentStr);
                        String desc = get(finalExtraParams, "desc");
                        if (isBlank(desc)) {
                            desc = contentStr;
                        }
                        if (isNotBlank(desc)) {
                            List<String> strings = HanLP.extractSummary(desc, 3);
                            desc = String.join(",", strings);
                        }

                        c.setDesc(desc);
                    }
            );
            dataPersistentService.batchSave(all1, spider);
//            javaCrawlerHotPointContentService.batchSave(all1);
        } else {

            log.error("没有db中的数据？异常url?:url{}", url);
            content.setUrl(url).setUrlMd5(urlMd5);
            content.setStatus(1);
            content.setType(get(extraParams, "type"));
            content.setTitle(get(extraParams, "title"));

            String contentStr = get(extraParams, "content");
            if (isBlank(contentStr)) {
                contentStr = get(extraParams, "title");
            }
            content.setContent(contentStr);
            content.setAddTime(LocalDateTime.now());
            String desc = get(extraParams, "desc");
            if (isBlank(desc)) {
                desc = contentStr;
            }
            content.setDesc(desc);
            content.setHotNum(get(extraParams, "hotNum"));
            content.setSortNum(get(extraParams, "sortNum"));
            dataPersistentService.save(content, spider);
//            javaCrawlerHotPointContentService.save(content);
        }
//        if (content != null) {
//            log.info("没有db中的数据？异常url?");
//            content = new JavaCrawlerHotPointContent();
//            content.setUrl(url).setUrlMd5(urlMd5);
////            return;
//        }
//        content.setType(get(extraParams, "type"));
//        content.setTitle(get(extraParams, "title"));
//        content.setContent(get(extraParams, "content"));
//        content.setAddTime(LocalDateTime.now());
//        content.setDesc(get(extraParams, "desc"));
//        content.setHotNum(get(extraParams, "hotNum"));
//        content.setSortNum(get(extraParams, "sortNum"));
//        javaCrawlerHotPointContentService.save(content);

    }

    public <T> T get(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }

        Object o = map.get(key);
        if (o == null) {
            return null;
        }
        return (T) map.get(key);
    }
}
