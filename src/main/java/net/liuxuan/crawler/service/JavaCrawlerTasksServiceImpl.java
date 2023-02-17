package net.liuxuan.crawler.service;

import net.liuxuan.crawler.entity.feedsdb.JavaCrawlerTasks;
import net.liuxuan.crawler.repository.feedsdb.JavaCrawlerTasksRepository;
import net.liuxuan.crawler.spring.jpa.service.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.service 包下xxxx工具
 * @date 2023/2/7
 **/
@Service
public class JavaCrawlerTasksServiceImpl extends BaseServiceImpl<JavaCrawlerTasks, Integer, JavaCrawlerTasksRepository> implements JavaCrawlerTasksService {
}
