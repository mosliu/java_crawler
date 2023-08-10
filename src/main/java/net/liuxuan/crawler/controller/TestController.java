package net.liuxuan.crawler.controller;

import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.constansts.TaskMapType;
import net.liuxuan.crawler.entity.feedsdb.JavaCrawlerTasks;
import net.liuxuan.crawler.service.JavaCrawlerTasksService;
import net.liuxuan.crawler.spring.dto.CommonResponseDto;
import net.liuxuan.crawler.utils.JacksonUtils;
import net.liuxuan.crawler.webmagic.domain.FieldExtractMethod;
import net.liuxuan.crawler.webmagic.domain.ProcessorDomain;
import net.liuxuan.crawler.webmagic.domain.SiteDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

import static net.liuxuan.crawler.constansts.TopHubSpiderName.TOPHUB_KUAISHOU;
import static net.liuxuan.crawler.constansts.TopHubSpiderName.TOPHUB_ZHIHU;
import static net.liuxuan.crawler.webmagic.domain.FieldExtractMethod.*;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.controller 包下xxxx工具
 * @date 2023/2/7
 **/
@Slf4j
@RestController
public class TestController {

    @Autowired
    JavaCrawlerTasksService javaCrawlerTasksService;

    @PostMapping(value = "/test")
    @ResponseBody
    public CommonResponseDto doTest(@RequestBody Map<String, Object> map) {
//        String dateStart = (String) map.get("dateStart");
        JavaCrawlerTasks task = new JavaCrawlerTasks();
        task.setTaskName(TOPHUB_ZHIHU);
        JavaCrawlerTasks javaCrawlerTasks = javaCrawlerTasksService.find(task);
        if (javaCrawlerTasks != null) {
            task = javaCrawlerTasks;
        }
        task.setTaskType(TaskMapType.TopHubTaskType.id()).setBaseUrl("https://www.zhihu.com/");
        SiteDomain siteDomain = new SiteDomain();
        siteDomain.setDomain("www.zhihu.com").setCharset("UTF-8")
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
                .setRetryTimes(3).setRetrySleepTime(3000).setTimeout(10000).setSleepTime(2000);
        String siteJson = JacksonUtils.obj2String(siteDomain);
        task.setSiteConf(siteJson);
        ProcessorDomain processorDomain = new ProcessorDomain();
        processorDomain.getFields().put("title", new FieldExtractMethod().setFieldName("title").setMethod(METHOD_CSS).setExpression(".QuestionHeader-title").setPos(1).setAttrName("text"));
        processorDomain.getFields().put("author", new FieldExtractMethod().setFieldName("author").setMethod(METHOD_CSS).setExpression("div .AuthorInfo div meta:nth-child(1)").setPos(1).setAttrName("content"));
        processorDomain.getFields().put("authorUrl", new FieldExtractMethod().setFieldName("authorUrl").setMethod(METHOD_CSS).setExpression("div .AuthorInfo div meta:nth-child(3)").setPos(1).setAttrName("url"));
        processorDomain.getFields().put("content", new FieldExtractMethod().setFieldName("content").setMethod(METHOD_CSS).setExpression(".QuestionRichText div span").setPos(1).setAttrName("text"));

        String rule = JacksonUtils.obj2String(processorDomain);
        task.setRule(rule);
        task.setCreateTime(LocalDateTime.now());
        task.setPipelines("HotPointContentDbPipeLine");
        task.setActive(true);
        JavaCrawlerTasks save = javaCrawlerTasksService.save(task);

        return CommonResponseDto.success();
    }


    @PostMapping(value = "/test1")
    @ResponseBody
    public CommonResponseDto doTest1(@RequestBody Map<String, Object> map) {
//        String dateStart = (String) map.get("dateStart");
        JavaCrawlerTasks task = new JavaCrawlerTasks();
        task.setTaskName(TOPHUB_KUAISHOU);
        JavaCrawlerTasks javaCrawlerTasks = javaCrawlerTasksService.find(task);
        if (javaCrawlerTasks != null) {
            task = javaCrawlerTasks;
        }
        task.setTaskType(TaskMapType.TopHubTaskType.id()).setBaseUrl("https://www.kuaishou.com/");
        SiteDomain siteDomain = new SiteDomain();
        siteDomain.setDomain("www.kuaishou.com").setCharset("UTF-8")
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
                .setRetryTimes(5).setRetrySleepTime(3000).setTimeout(10000).setSleepTime(5000);
        String siteJson = JacksonUtils.obj2String(siteDomain);
        task.setSiteConf(siteJson);

        ProcessorDomain processorDomain = new ProcessorDomain();
        processorDomain.getFields().put("content",
                new FieldExtractMethod().setFieldName("content").setMethod(METHOD_XPATH)
                        .setExpression("//*[@id=\"root\"]/div[1]/div[2]/div/div/div[1]/div[3]/div/div[1]/div/h1/span/span[2]/span")
                        .setChild(FieldExtractMethod.n().setMethod(METHOD_SMART_CONTENT)));
//        processorDomain.getFields().put("content", new FieldExtractMethod().setFieldName("content").setMethod(METHOD_SMART_CONTENT));


        String rule = JacksonUtils.obj2String(processorDomain);
        task.setRule(rule);
        task.setCreateTime(LocalDateTime.now());
        task.setPipelines("HotPointContentDbPipeLine");
        task.setActive(true);
        JavaCrawlerTasks save = javaCrawlerTasksService.save(task);

        return CommonResponseDto.success();
    }

}
