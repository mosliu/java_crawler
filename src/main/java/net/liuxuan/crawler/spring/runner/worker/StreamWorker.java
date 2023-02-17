package net.liuxuan.crawler.spring.runner.worker;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @param <S> 输入流的类型
 * @param <R> 输出流的类型
 * @author Liuxuan
 * @version v1.0.0
 * @description 流式处理worker基类
 * @date 2019-05-20
 **/
@Slf4j
abstract public class StreamWorker<S, R> extends StopAbleThread {

    Integer runFlagRecheckTime = 500;

    /**
     * 标志停止流
     */
    protected volatile boolean stopStreamFlag = false;

    protected BlockingQueue<S> inBlockQueue;
    protected List<BlockingQueue<R>> outBlockQueueList = new LinkedList<>();

    public void setRunFlagRecheckTime(Integer runFlagRecheckTime) {
        this.runFlagRecheckTime = runFlagRecheckTime;
    }

    public void setInBlockQueue(BlockingQueue<S> inBlockQueue) {
        this.inBlockQueue = inBlockQueue;
    }

    public void addOutBlockQueue(BlockingQueue<R> outBlockQueue) {
        if (this.outBlockQueueList != null) {
            if (this.outBlockQueueList.contains(outBlockQueue)) {
                return;
            }
        }
        this.outBlockQueueList.add(outBlockQueue);
    }

    @Override
    public void run() {

        prepareRun();
        log.info("{} starting", this.getName());
        while (!isInterrupted()) {
            try {
                while (runFlag) {

                    if (!checkInBlockQueueExists()) {
                        setRunFlag(false);
                    } else {
//                        BigDataMessage take = inBlockQueue.take();
                        S take = getCurrentMsg();
                        //对数据进行处理
                        R processed = null;
                        processed = processMessage(take);
                        if (processed != null) {
                            nextStep(processed);
                        }
                    }
                }
                log.info("{} stopped", this.getName());
                //即使关掉runflag也不退出进程。
                Thread.sleep(runFlagRecheckTime);
            } catch (InterruptedException e) {
                log.info("{} : {} 中断，退出", this.getClass().getName(), this.getName());
                return;
            } catch (Exception ex) {
                log.error("处理时异常发生，线程会死掉！需要检查！  " + this.getClass().getName() + "," + this.getName(), ex);
//                logError("程序调度异常", "处理时异常发生，线程会死掉！需要检查！", ex);
            }
        }
    }

    protected boolean checkInBlockQueueExists() {
        return inBlockQueue != null;
    }


    /**
     * 获取数据，默认是从inQueue中获取的，无则阻塞。
     * 可重载
     *
     * @return
     * @throws InterruptedException
     */
    protected S getCurrentMsg() throws InterruptedException {
        return inBlockQueue.take();
    }


    /**
     * 在主循环run之前的准备工作
     */
    public void prepareRun() {

    }


    /**
     * 对msg本身进行处理
     *
     * @param msg
     */
    abstract public R processMessage(S msg);

    /**
     * 下一步处理，默认是放入所有下一步的queue中
     * 可重载
     *
     * @param take
     * @throws InterruptedException
     */
    protected void nextStep(R take) throws InterruptedException {
        if (take == null) {
            //空的则抛弃
            return;
        }
        if (outBlockQueueList != null) {
            //如空只取出即可
            for (int i = 0; i < outBlockQueueList.size(); i++) {
                outBlockQueueList.get(i).put(take);
            }
        }
    }

    /**
     * 终止
     *
     * @return
     */
    public boolean stopMe() {
        stopStreamFlag = true;
//        for (int i = 0; i < outBlockQueueList.size(); i++) {
//            if(outBlockQueueList.get(i).size()>0) return false;
//        }
        if (!isInBlockQueueEmpty()) {
//            log.info("队列仍有数据，无法结束");
            log.info("InBlockQueue：{}，Size:{}", this.getName(), inBlockQueue.size());
            return false;
        }
        //这里应该加上前一个步骤的CountDownLatch
        setRunFlag(false);
        log.info("{},已停止进程！！", this.getName());
        this.getThreadStop().countDown();
        return true;
    }

    protected boolean isInBlockQueueEmpty() {
        return inBlockQueue.size() == 0;
    }

}
