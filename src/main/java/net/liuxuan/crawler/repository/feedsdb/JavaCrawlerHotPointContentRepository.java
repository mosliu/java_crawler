package net.liuxuan.crawler.repository.feedsdb;

import net.liuxuan.crawler.entity.feedsdb.JavaCrawlerHotPointContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JavaCrawlerHotPointContentRepository extends JpaRepository<JavaCrawlerHotPointContent, Integer>, JpaSpecificationExecutor<JavaCrawlerHotPointContent> {

}