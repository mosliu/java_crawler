package net.liuxuan.crawler.runner.worker;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.runner.worker 包下xxxx工具
 * @date 2023/2/2
 **/
@Data
@Accessors(chain = true)
public class WorkerConfig {
    long scheduleMills = 0L;
}
