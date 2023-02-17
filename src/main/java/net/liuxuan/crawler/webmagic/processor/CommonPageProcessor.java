package net.liuxuan.crawler.webmagic.processor;

import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.webmagic.domain.FieldExtractMethod;
import net.liuxuan.crawler.webmagic.domain.ProcessorDomain;
import net.liuxuan.crawler.webmagic.domain.SiteDomain;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.processors 包下xxxx工具
 * @date 2023/2/6
 **/
@Slf4j
public class CommonPageProcessor implements PageProcessor {


    private SiteDomain siteInfo;
    private ProcessorDomain processorInfo;

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site
            .me()
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
        log.info("{}正在处理页面：{}", site.getDomain(), page.getUrl().toString());
        if (siteInfo == null) {
            log.error("siteInfo is null");
            //skip为true，则不会通知pipeline处理
            page.setSkip(true);
            return;
        }
        if (processorInfo == null) {
            log.error("processorInfo is null");
            //skip为true，则不会通知pipeline处理
            page.setSkip(true);
            return;
        }
        if (processorInfo.getFields() == null || processorInfo.getFields().size() == 0) {
            log.error("processorInfo.fields is null");
            //skip为true，则不会通知pipeline处理
            page.setSkip(true);
            return;
        }
//        if(!page.getUrl().toString().startsWith("https://github.com/code4craft")){
//            //skip为true，则不会通知pipeline处理
//            page.setSkip(true);
//        }
        if (isBlank(processorInfo.getUrlRegex()) || page.getUrl().regex(processorInfo.getUrlRegex()).match()) {
            if (isBlank(processorInfo.getUrlRegex())) {
                log.warn("{} processorInfo.urlRegex is null", siteInfo.getDomain());
            }
            processorInfo.getFields().forEach((k, v) -> {
                Selectable node = getNode(page.getHtml(), v);
                if (node != null) {
                    //顺利获取到node
                    page.putField(k, getFieldValue(node, v));
                } else {
                    //未获取到node
                    log.info("未获取到字段:{}的node", k);
                }
            });
        } else {
            log.info("page url:{} not hit processorInfo.urlRegex:{}", page.getUrl(), processorInfo.getUrlRegex());
        }

        //列表页
//        if (page.getUrl().regex(metaInfo.getListRegex()).match() || page.getRequest().getUrl().equals(metaInfo.getEntryUrl())) {
//            page.addTargetRequests(page.getHtml().links().regex(metaInfo.getListRegex()).all());
//            metaInfo.getFields().forEach((k, v) -> {
//                page.putField(k, page.getHtml().xpath(v).all());
//            });
//        } else {
//            //正文页
//            metaInfo.getContentFields().forEach((k, v) -> {
//                page.putField(k, page.getHtml().xpath(v).all());
//            });
//        }
    }

    /**
     * 从获取的节点中，获取最终的数据
     *
     * @param node
     * @param method
     * @return
     */
    public Object getFieldValue(Selectable node, FieldExtractMethod method) {
        switch (method.getFinalResultType()) {
            case FieldExtractMethod.FINAL_RESULT_TYPE_GET:
                return node.get();
            case FieldExtractMethod.FINAL_RESULT_TYPE_ALL:
                return node.all();
            default:
                return null;
        }
    }


    /**
     * 按规则解析node
     *
     * @param node
     * @param method
     * @return
     */
    public Selectable getNode(Selectable node, FieldExtractMethod method) {
        if (method == null || node == null) {
            return null;
        }
        Selectable nextNode = null;
        switch (method.getMethod()) {
            case FieldExtractMethod.METHOD_XPATH:
                nextNode = node.xpath(method.getExpression());
                break;
            case FieldExtractMethod.METHOD_CSS:
                if (method.getPos() == FieldExtractMethod.POS_NONE) {
                    nextNode = node.css(method.getExpression());
                } else {
                    nextNode = node.css(method.getExpression(), method.getAttrName());
                }
                break;
            case FieldExtractMethod.METHOD_REGEX:
                nextNode = node.regex(method.getExpression());
                break;
            case FieldExtractMethod.METHOD_SMART_CONTENT:
                nextNode = node.smartContent();
                break;
            default:
                nextNode = null;
        }
        if (method.hasChild()) {
            return getNode(nextNode, method.getChild());
        }
        return nextNode;
    }


    @Override
    public Site getSite() {
        return site;
    }


    public void setMetaInfo(SiteDomain siteInfo, ProcessorDomain processorInfo) {
        this.siteInfo = siteInfo;
        getSite()
                .setRetryTimes(siteInfo.getRetryTimes())
                .setRetrySleepTime(siteInfo.getRetrySleepTime())
                .setDomain(siteInfo.getDomain())
                .setSleepTime(siteInfo.getSleepTime())
                .setTimeOut(siteInfo.getTimeout())
                .setCharset(siteInfo.getCharset())
                .setUserAgent(siteInfo.getUserAgent())
                .setCycleRetryTimes(siteInfo.getCycleRetryTimes()) //错误重试次数
//                .addHeader("Cookie", "")
        ;
        if (siteInfo.getHeaders() != null && siteInfo.getHeaders().size() > 0) {
            siteInfo.getHeaders().forEach((k, v) -> {
                getSite().addHeader(k, v);
            });
        }


        this.processorInfo = processorInfo;
//        this.metaInfo = metaInfo;
    }


}
