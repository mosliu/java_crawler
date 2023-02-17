package net.liuxuan.crawler.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 从hubtop获取的数据的实体
 * @date 2023/2/1
 **/
@Data
@Accessors(chain = true)
public class HubTopEntity {
    String title;
    String hotValue;
    String url;
    String itemid;
    String sortid;

    boolean needCrawl = true;
}
