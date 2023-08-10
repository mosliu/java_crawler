package net.liuxuan.crawler.service;

import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.entity.feedsdb.JavaCrawlerHotPointContent;
import net.liuxuan.crawler.entity.feedsdb.OpenHotPointContentHis;
import net.liuxuan.crawler.webmagic.CommonSpider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 数据持久化服务
 * @date 2023/5/25
 **/
@Service
@Slf4j
public class DataPersistentService {
    @Autowired
    JavaCrawlerHotPointContentService javaCrawlerHotPointContentService;

    @Autowired
    OpenHotPointContentHisService openHotPointContentHisService;

    public void batchSave(List<JavaCrawlerHotPointContent> all1, CommonSpider task) {
        if (all1 == null) {
            return;
        }
        all1.forEach(e -> e.setDesc(StringUtils.substring(e.getDesc(), 0, 250)));
//        if(task.)
        javaCrawlerHotPointContentService.batchSave(all1);
        if (task != null) {
            if (task.getSPIDER_INFO() != null && task.getSPIDER_INFO().isSendToOldTable()) {
//                List<OpenHotPointContentHis> openHotPointContentHis = openHotPointContentHisService.findAll();
                List<OpenHotPointContentHis> openHotPointContentHisArrayList = new ArrayList<>();
                for (JavaCrawlerHotPointContent javaCrawlerHotPointContent : all1) {
                    OpenHotPointContentHis content = new OpenHotPointContentHis();
                    BeanUtils.copyProperties(javaCrawlerHotPointContent, content);
                    openHotPointContentHisArrayList.add(content);
                }
                try {
                    //字段太短，应周艳蕊要求，截断，230619
                    openHotPointContentHisArrayList.stream().forEach(e -> {
                        if (e.getHotNum() != null) {
                            String substring = StringUtils.substring(e.getHotNum(), 0, 15);
                            e.setHotNum(substring);
                        }
                    });
                    openHotPointContentHisService.batchSave(openHotPointContentHisArrayList);
                } catch (Exception e) {
                    log.error("batchSave error!", e);
                }
            }
        }
    }

    public void save(JavaCrawlerHotPointContent content, CommonSpider task) {
        content.setDesc(StringUtils.substring(content.getDesc(), 0, 250));
        javaCrawlerHotPointContentService.save(content);
        if (task != null) {
            if (task.getSPIDER_INFO() != null && task.getSPIDER_INFO().isSendToOldTable()) {
                OpenHotPointContentHis content2 = new OpenHotPointContentHis();
                BeanUtils.copyProperties(content, content2);
                try {
                    //字段太短，应周艳蕊要求，截断，230619
                    content2.setHotNum(StringUtils.substring(content2.getHotNum(), 0, 15));
                    openHotPointContentHisService.save(content2);
                } catch (Exception e) {
                    log.error("batchSave error!", e);
                }
            }
        }
    }
}
