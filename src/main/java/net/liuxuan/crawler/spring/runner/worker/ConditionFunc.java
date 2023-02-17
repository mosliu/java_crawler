package net.liuxuan.crawler.spring.runner.worker;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2019-05-21
 **/
@FunctionalInterface
public interface ConditionFunc<T> {
    boolean getCondition(T take);
}
