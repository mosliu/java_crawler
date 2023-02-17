package net.liuxuan.crawler.spring.redis.config;

import lombok.Data;

import java.util.List;

@Data
public class RedisProperty {

    private String host;

    private int port;

    private int database;

    private String password;

    private String masterName;

    private List<String> sentinels;

    private List<String> clusters;
}
