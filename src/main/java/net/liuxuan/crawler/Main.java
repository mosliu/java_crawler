package net.liuxuan.crawler;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan 包下xxxx工具
 * @date ${DATE}
 **/
@Slf4j
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        log.info("hello");
        log.error("world");
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("Hello World !!");
    }
}