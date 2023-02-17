package net.liuxuan.crawler.spring.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2022-02-25
 **/
@Data
@Component
@ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
public class RedisLettucePoolProperty {
    int maxActive = 50;
    int maxWait = -1;
    int maxIdle = 20;
    int minIdle = 0;
}
