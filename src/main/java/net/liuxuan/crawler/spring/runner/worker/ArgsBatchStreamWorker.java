package net.liuxuan.crawler.spring.runner.worker;

import lombok.extern.slf4j.Slf4j;

/**
 * @param <S> 输入流的类型
 * @param <R> 输出流的类型
 * @param <C> 配置
 * @author Liuxuan
 * @version v1.0.0
 * @description 流式处理worker基类（附加输入args）
 * @date 2019-05-20
 */
@Slf4j
abstract public class ArgsBatchStreamWorker<S, R, C> extends BatchStreamWorker<S, R> {
    abstract public void initArgs(C config) throws NoArgsException;
}
