package net.liuxuan.crawler.spring.runner.worker;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.spring.runner.worker 包下xxxx工具
 * @date 2023/2/2
 **/
abstract public class ArgsStopAbleThread<C> extends StopAbleThread {
    abstract public void initArgs(C config) throws NoArgsException;
}
