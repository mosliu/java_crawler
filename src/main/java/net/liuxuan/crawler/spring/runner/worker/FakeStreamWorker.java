package net.liuxuan.crawler.spring.runner.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 用于保证当某个服务设定为不服务时，可以消耗掉之前的服务放入blockqueue中的数据，或将数据传入下一个服务中
 * @date 2019-05-17
 **/
@Slf4j
@Component
@Scope("prototype")
//public class FakeStreamWorker extends StreamWorker<BigDataMessage, BigDataMessage> {
public class FakeStreamWorker<S> extends StreamWorker<S, S> {

    @Override
    public S processMessage(S msg) {
        //什么都不处理，直接放入下一步
        return msg;
    }


}
