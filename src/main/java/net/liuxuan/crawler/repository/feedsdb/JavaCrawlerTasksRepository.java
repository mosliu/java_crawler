package net.liuxuan.crawler.repository.feedsdb;

import net.liuxuan.crawler.entity.feedsdb.JavaCrawlerTasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JavaCrawlerTasksRepository extends JpaRepository<JavaCrawlerTasks, Integer>, JpaSpecificationExecutor<JavaCrawlerTasks> {

}