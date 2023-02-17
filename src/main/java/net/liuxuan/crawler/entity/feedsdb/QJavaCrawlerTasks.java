package net.liuxuan.crawler.entity.feedsdb;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;

import javax.annotation.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QJavaCrawlerTasks is a Querydsl query type for JavaCrawlerTasks
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJavaCrawlerTasks extends EntityPathBase<JavaCrawlerTasks> {

    private static final long serialVersionUID = -480200181L;

    public static final QJavaCrawlerTasks javaCrawlerTasks = new QJavaCrawlerTasks("javaCrawlerTasks");

    public final BooleanPath active = createBoolean("active");

    public final StringPath baseUrl = createString("baseUrl");

    public final StringPath comment = createString("comment");

    public final DateTimePath<java.time.LocalDateTime> createTime = createDateTime("createTime", java.time.LocalDateTime.class);

    public final StringPath downloader = createString("downloader");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath pipelines = createString("pipelines");

    public final StringPath processor = createString("processor");

    public final StringPath rule = createString("rule");

    public final StringPath siteConf = createString("siteConf");

    public final StringPath taskName = createString("taskName");

    public final NumberPath<Integer> taskType = createNumber("taskType", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> updateTime = createDateTime("updateTime", java.time.LocalDateTime.class);

    public QJavaCrawlerTasks(String variable) {
        super(JavaCrawlerTasks.class, forVariable(variable));
    }

    public QJavaCrawlerTasks(Path<? extends JavaCrawlerTasks> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJavaCrawlerTasks(PathMetadata metadata) {
        super(JavaCrawlerTasks.class, metadata);
    }

}

