package net.liuxuan.crawler.repository.feedsdb;

import net.liuxuan.crawler.entity.feedsdb.OpenHotPointContentHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OpenHotPointContentHisRepository extends JpaRepository<OpenHotPointContentHis, Integer>, JpaSpecificationExecutor<OpenHotPointContentHis> {

}