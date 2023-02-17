package net.liuxuan.crawler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.config 包下xxxx工具
 * @date 2023/2/10
 **/
@Data
@ConfigurationProperties(prefix = "crawler")
@Component
public class CrawlerConfig {
    boolean backendRunnerEnable;
    boolean tophubRunnerEnable;
}
