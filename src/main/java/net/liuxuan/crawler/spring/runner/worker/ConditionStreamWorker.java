package net.liuxuan.crawler.spring.runner.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 用于保证当某个服务设定为不服务时，可以消耗掉之前的服务放入blockqueue中的数据，或将数据传入下一个服务中
 * @date 2019-05-17
 **/
@Slf4j
@Component
@Scope("prototype")
public class ConditionStreamWorker<T> extends StreamWorker {

    Predicate<T> func;

    public void setFunc(Predicate<T> func) {
        this.func = func;
    }


    @Override
    protected void nextStep(Object take) throws InterruptedException {
        if (func.test((T) take)) {
            super.nextStep(take);
        }
    }

    @Override
    public Object processMessage(Object msg) {
        return msg;
        //什么都不处理，直接放入下一步
    }
}
