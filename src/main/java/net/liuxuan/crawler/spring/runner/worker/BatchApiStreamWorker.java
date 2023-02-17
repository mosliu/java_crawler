package net.liuxuan.crawler.spring.runner.worker;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2019-10-30
 **/
@Slf4j
public abstract class BatchApiStreamWorker<T, R> extends StreamWorker<T, R> {

    protected Long lasttime = System.currentTimeMillis();
    /**
     * 最小的Size
     */
    protected Integer batchMinSize = 20;
    /**
     * 最大等待
     */
    protected Long reqMaxIdle = 15000L;

    protected LinkedBlockingQueue<T> databuffer = new LinkedBlockingQueue<>(1000);

    protected boolean trigger = false;

    @Override
    public void run() {

        prepareRun();
        log.info("{} starting", this.getName());
        while (!isInterrupted()) {
            try {
                while (runFlag) {

                    if (inBlockQueue == null && databuffer.size() == 0) {
                        setRunFlag(false);
                    } else {
//                        BigDataMessage take = inBlockQueue.take();
                        trigger = false;
                        T take = null;
//                        if (inBlockQueue.size() > 0) {
                        if (!inBlockQueue.isEmpty()) {
                            take = getCurrentMsg();
                            //对数据进行处理
                            trigger = true;
                        }
                        if (databuffer.size() > 0 && System.currentTimeMillis() - lasttime > reqMaxIdle) {
                            trigger = true;
                            take = null;
                        }

                        //执行批量处理
                        if (trigger) {
                            R processed = processMessage(take);
                            if (processed != null) {
                                nextStep(processed);
                            }
                        } else {
                            sleep(100);
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
                log.error("程序调度异常", "处理时异常发生，线程会死掉！需要检查！", ex);
            }
        }
    }


    /**
     * 获取数据，默认是从inQueue中获取的，无则阻塞。
     * 可重载
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    protected T getCurrentMsg() throws InterruptedException {

        if (stopStreamFlag && inBlockQueue.size() == 0) {
            //放一个进入队列，放置批处理时卡住
//            TagBigDataMessage ticktock = new TagBigDataMessage();
//            return ticktock;
            return null;
        }

        if (inBlockQueue.size() == 0 && databuffer.size() > 0 && System.currentTimeMillis() - lasttime > reqMaxIdle) {
            return null;
        }


        return inBlockQueue.poll(100, TimeUnit.MILLISECONDS);
    }


    /**
     * proceessMessage 通用的处理过程，重载后应该重新调一遍，
     * 必须返回null
     *
     * @param msg
     * @return
     */
    @Override
    public R processMessage(T msg) {
        //来数据后放入缓冲队列
        Long now = System.currentTimeMillis();
        try {

//            log.info("new msg");
            if (msg == null) {
                if (stopStreamFlag) {

                } else {
//                    log.warn("Got null msg!");
                }
            } else {
                databuffer.put(msg);
            }

            if (databuffer.size() >= batchMinSize || now - lasttime > reqMaxIdle) {
//                if (databuffer.size() == 0) {
//                    return;
//                }
                lasttime = System.currentTimeMillis();


                LinkedList<R> bdList = new LinkedList<>();
//                databuffer.drainTo(bdList);
                while (databuffer.size() > 0) {
                    R r = t2r(databuffer.take());
                    bdList.add(r);
                }
                if (bdList.size() == 0) {
                    //多线程时，有时取不到
                    return null;
                }
                doBatch(bdList);
//
//            } else if (this.inBlockQueue.size() > 0) {
//                //donothing, continue
////                sleep(property.getNothingToParseWait());
//            } else {
//                sleep(100);

            }

        } catch (InterruptedException e) {
//            log.error("error", e);
            log.info("{} : {} 中断，退出", this.getClass().getName(), this.getName());
        }
//        return msg;
        return null;

    }

    protected abstract R t2r(T msg);

    protected void doBatch(List<R> bdList) throws InterruptedException {
        List<R> ts = doBatchProcess(bdList);
        if (ts != null) {
            for (R e : ts) {
                nextStep(e);
            }
        }
    }

    ;

    /**
     * 批量的数据做处理，返回的list会进入下一步
     *
     * @param bdList
     * @return
     */
    protected abstract List<R> doBatchProcess(List<R> bdList);

//    @Override
//    protected void nextStep(R message) {
//        //因为需要等数据完成后再发送，不再走默认的nextStep 而是单独调用nextStep2
//
//    }
//
//    protected void nextStep2(R message) {
//        try {
//            //如果是新华智云的新闻的话 额外发一份给ES的kafka
////            if (message.getMsgFromType() == BigDataMsgType.XHZY_NEWS || message.getMsgFromType() == BigDataMsgType.DZSPIDER) {
////                blockQueueForZhengwuKafka.put(message);
////            }
//
//            super.nextStep(message);
////            blockQueueForKafka.put(message);//发送大众kafka
////            hbaseWriterBlockQueue.put(message);//插入hbase
//        } catch (InterruptedException e) {
//            log.info("{} : {} 中断，退出", this.getClass().getName(), this.getName());
//        }
//
//    }

}
