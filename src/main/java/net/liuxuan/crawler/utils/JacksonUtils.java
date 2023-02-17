package net.liuxuan.crawler.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class JacksonUtils {

    private static final ObjectMapper OBJECT_MAPPER = create();  //thread safe的
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static ObjectMapper create() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module());

        //process java time format
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));

        mapper.registerModule(javaTimeModule);

        mapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true); //空字符串序列化为null
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true); //有未知属性会报错
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }

    public static String obj2String(Object object) {
        try {
            if (object == null) {
                return "";
            }
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            log.warn("obj to json string error|objectType=" + object.getClass().getCanonicalName(), e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T str2Object(String jsonString, TypeReference<T> type) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, type);
        } catch (IOException e) {
            log.warn("json string to object error by type|string={}|type={}", jsonString, type.getClass().getCanonicalName(), e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T str2Object(String jsonString, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (IOException e) {
            log.warn("json string to object error by class|str= {}|type={}", jsonString, clazz.getCanonicalName(), e);
            throw new RuntimeException(e);
        }
    }
}
