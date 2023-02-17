package net.liuxuan.crawler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
@ComponentScan(basePackages = {"net.liuxuan"})
public class JavaCrawlerApplication {

    public static void main(String[] args) {
//        SpringApplication.run(JavaCrawlerApplication.class, args);

        log.warn("main start");
        SpringApplication app = new SpringApplication(JavaCrawlerApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
        log.warn("main end");

    }

}
