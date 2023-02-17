package net.liuxuan.crawler.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2019-04-02
 **/
@Component(value = "springContext")
@Order(10)
public class SpringContext implements ApplicationContextAware {


    protected static ApplicationContext context;
    public static String appname;

    @Value("${spring.application.name:AppnameNotSet}")
    public void setAppname(String _appname) {
        appname = _appname;
    }


//    protected ApplicationContext thiscontext;
//    @PostConstruct
//    private void init(){
//        context = thiscontext;
//    }

    public static void setContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @Autowired
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * 获取对象
     * 这里重写了bean方法，起主要作用
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) context.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> clz) throws BeansException {
        return context.getBean(name, clz);
    }

    public static <T> T getBean(Class<T> clz) throws BeansException {
        return context.getBean(clz);
    }

    public static Object getMessage(String key) {
        return context.getMessage(key, null, Locale.getDefault());
    }

    public static Environment getEnvironment() {
        return context.getEnvironment();
    }

    public static String getProperty(String key) {
        return context.getEnvironment().getProperty(key);
    }

    public static String getAppName() {
        return appname;
    }


}