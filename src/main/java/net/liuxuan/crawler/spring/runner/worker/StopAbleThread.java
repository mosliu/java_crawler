package net.liuxuan.crawler.spring.runner.worker;

import java.util.concurrent.CountDownLatch;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2019-04-15
 **/
public abstract class StopAbleThread extends Thread {
//    List<StopAbleThread> stopAbleThreads = new ArrayList<>();
//
//    public void startThread(Class clz, int count, ThreadPoolTaskExecutor runnerExecutor) {
//        for (int i = 0; i < count; i++) {
//            StopAbleThread c = (StopAbleThread) SpringContext.getBean(clz);
//            runnerExecutor.execute(c);
//            stopAbleThreads.add(c);
//        }
//    }
    protected CountDownLatch threadStop;


    protected volatile boolean runFlag = true;

    public boolean isRunFlag() {
        return runFlag;
    }

    public void setRunFlag(boolean runFlag) {
        this.runFlag = runFlag;
    }


    public CountDownLatch getThreadStop() {
        return threadStop;
    }

    public void setThreadStop(CountDownLatch threadStop) {
        this.threadStop = threadStop;
    }

    public boolean stopMe() {

        setRunFlag(false);
        threadStop.countDown();
        return true;
    }


}
