package net.liuxuan.crawler.webmagic.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description spider领域的属性
 * @date 2023/2/8
 **/
@Data
@Accessors(chain = true)
public class SpiderDomain {
    boolean sendToOldTable = false;
    String taskName;
}
