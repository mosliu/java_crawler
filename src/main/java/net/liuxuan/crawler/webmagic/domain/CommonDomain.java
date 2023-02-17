package net.liuxuan.crawler.webmagic.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CommonDomain implements Serializable {
    private String id;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;

}