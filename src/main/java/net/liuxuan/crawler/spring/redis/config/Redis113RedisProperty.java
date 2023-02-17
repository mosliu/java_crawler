package net.liuxuan.crawler.spring.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.redis.redis113")
@EnableConfigurationProperties(Redis113RedisProperty.class)
public class Redis113RedisProperty extends RedisProperty {
}
