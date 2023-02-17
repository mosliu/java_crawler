package net.liuxuan.crawler.webmagic.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import net.liuxuan.crawler.utils.JacksonUtils;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.domain 包下xxxx工具
 * @date 2023/2/7
 **/
@Data
@Accessors(chain = true)
public class ProcessorDomain {
    Map<String, FieldExtractMethod> fields = new HashMap<>();
    String urlRegex;

    public static ProcessorDomain fromJson(String rule) {
        if (isBlank(rule)) {
            return null;
        }

        ProcessorDomain processorDomain;
        try {
            processorDomain = JacksonUtils.str2Object(rule, ProcessorDomain.class);
        } catch (Exception e) {
            processorDomain = null;
        }

        return processorDomain;
    }
}
