package net.liuxuan.crawler.spring.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2019-06-14
 **/
//@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryPrimary",
        transactionManagerRef = "transactionManagerPrimary",
        basePackages = {"net.liuxuan.crawler.repository.information"}
//        basePackageClasses = {VideoConvertRepository.class}
) //设置Repository所在位置
//@EntityScan(basePackages = {"com.sddzinfo.dzyq.videoconverter.entity"})
public class JpaConfigurationInformation {
    @Autowired
    @Qualifier("informationDataSource")
    private DataSource informationDataSource;

    //    @Primary
    @Bean(name = "entityManagerInformation")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryPrimary(builder).getObject().createEntityManager();
    }

    //    @Primary
    @Bean(name = "entityManagerFactoryInformationo")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {
//        Config config = ConfigService.getAppConfig();
        LocalContainerEntityManagerFactoryBean entityManagerFactory
                = builder
                .dataSource(informationDataSource)
                .packages("net.liuxuan.crawler.entity.information")//设置实体类所在位置
                .persistenceUnit("primaryPersistenceUnit")//持久化单元创建一个默认即可，多个便要分别命名
                .build();
        Properties props = new Properties();
        props.setProperty("hibernate.hbm2ddl.auto", "validate");

        entityManagerFactory.setJpaProperties(props);
        return entityManagerFactory;
    }

    //    @Primary
    @Bean(name = "transactionManagerInformation")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryPrimary(builder).getObject());
    }
}
