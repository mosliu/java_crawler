package net.liuxuan.crawler.entity.feedsdb;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import javax.annotation.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QJavaCrawlerHotPointContent is a Querydsl query type for JavaCrawlerHotPointContent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJavaCrawlerHotPointContent extends EntityPathBase<JavaCrawlerHotPointContent> {

    private static final long serialVersionUID = 1067382771L;

    public static final QJavaCrawlerHotPointContent javaCrawlerHotPointContent = new QJavaCrawlerHotPointContent("javaCrawlerHotPointContent");

    public final DateTimePath<java.time.LocalDateTime> addTime = createDateTime("addTime", java.time.LocalDateTime.class);

    public final StringPath cityName = createString("cityName");

    public final StringPath content = createString("content");

    public final StringPath desc = createString("desc");

    public final StringPath gid = createString("gid");

    public final StringPath groupData = createString("groupData");

    public final StringPath hotNum = createString("hotNum");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> num = createNumber("num", Integer.class);

    public final StringPath sortNum = createString("sortNum");

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public final StringPath title = createString("title");

    public final StringPath type = createString("type");

    public final StringPath url = createString("url");

    public final StringPath urlMd5 = createString("urlMd5");

    public QJavaCrawlerHotPointContent(String variable) {
        super(JavaCrawlerHotPointContent.class, forVariable(variable));
    }

    public QJavaCrawlerHotPointContent(Path<? extends JavaCrawlerHotPointContent> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJavaCrawlerHotPointContent(PathMetadata metadata) {
        super(JavaCrawlerHotPointContent.class, metadata);
    }

}

