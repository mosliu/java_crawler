package net.liuxuan.crawler.spring.db;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 多数据源配置
 * @date 2023/2/6
 **/
@Configuration
@Slf4j
public class DatasourceConfig {

    /************************************feedsdb库********************************************/
    @Primary
    @Bean
    @ConditionalOnProperty(name = "db.feedsdb.enable", havingValue = "true", matchIfMissing = false)
    @ConfigurationProperties(prefix = "db.feedsdb")
    public DataSourceProperties feedsdbDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Primary
    @Bean(name = "feedsdbDataSource")
    @ConditionalOnProperty(name = "db.feedsdb.enable", havingValue = "true", matchIfMissing = false)
//    @ConfigurationProperties(prefix = "db.feedsdb")
    public DataSource feedsdbDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
//        return new HikariDataSource();
//        return DruidDataSourceBuilder.create().build();
        //return DataSourceBuilder.create().build();
    }
//    @Primary
//    @Bean("primaryDataSource")
//    @Qualifier(value = "primaryDataSource")
//    // 留意下面这行
//    @ConfigurationProperties(prefix = "db.primary.hikari")
//    public HikariDataSource primaryDataSource() {
//        return primaryDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
//    }

    @Primary
    @Bean(name = "feedsdbJdbcTemplate")
    @ConditionalOnProperty(name = "db.feedsdb.enable", havingValue = "true", matchIfMissing = false)
    public JdbcTemplate feedsdbJdbcTemplate(@Qualifier("feedsdbDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Primary
    @Bean(name = "feedsdbNamedParameterJdbcTemplate")
    @ConditionalOnProperty(name = "db.feedsdb.enable", havingValue = "true", matchIfMissing = false)
    public NamedParameterJdbcTemplate feedsdbNamedParameterJdbcTemplate(@Qualifier("feedsdbDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }



    /************************************83上的information_**的库********************************************/


    @Bean
    @ConditionalOnProperty(name = "db.information.enable", havingValue = "true", matchIfMissing = false)
    @ConfigurationProperties(prefix = "db.information")
    public DataSourceProperties informationDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(name = "informationDataSource")
    @ConditionalOnProperty(name = "db.information.enable", havingValue = "true", matchIfMissing = false)
//    @ConfigurationProperties(prefix = "db.feedsdb")
    public DataSource informationDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
//        return new HikariDataSource();
//        return DruidDataSourceBuilder.create().build();
        //return DataSourceBuilder.create().build();
    }

//    @Bean(name = "informationDataSource")
//    @ConfigurationProperties(prefix = "db.information")
//    @ConditionalOnProperty(name = "db.information.enable", havingValue = "true", matchIfMissing = false)
//    public DataSource informationDataSource() {
//        return new HikariDataSource();
//        //return DataSourceBuilder.create().build();
//    }

    @Bean(name = "informationJdbcTemplate")
    @ConditionalOnProperty(name = "db.information.enable", havingValue = "true", matchIfMissing = false)
    public JdbcTemplate informationJdbcTemplate(@Qualifier("informationDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "informationNamedParameterJdbcTemplate")
    @ConditionalOnProperty(name = "db.information.enable", havingValue = "true", matchIfMissing = false)
    public NamedParameterJdbcTemplate informationNamedParameterJdbcTemplate(@Qualifier("informationDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }


}
