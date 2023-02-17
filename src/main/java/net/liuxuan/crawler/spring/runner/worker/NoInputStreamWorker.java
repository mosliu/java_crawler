package net.liuxuan.crawler.spring.runner.worker;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2021-10-14
 **/
public abstract class NoInputStreamWorker<R> extends StreamWorker<String, R> {


    @Override
    protected boolean checkInBlockQueueExists() {
        return true;
    }

    /**
     * 获取数据，默认是从inQueue中获取的，无则阻塞。
     * 可重载
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    protected String getCurrentMsg() throws InterruptedException {
        return "";
    }

    @Override
    protected boolean isInBlockQueueEmpty() {
        return true;
    }

    @Override
    public R processMessage(String msg) {
        return processMessage();
    }

    public abstract R processMessage();
}
