package net.liuxuan.crawler.spring.runner.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 用于消费掉队列
 * @date 2019-05-17
 **/
@Slf4j
@Component
@Scope("prototype")
public class ConsumStreamWorker<S, R> extends StreamWorker<S, R> {
    @Override
    public R processMessage(S msg) {
        return null;
    }
}
