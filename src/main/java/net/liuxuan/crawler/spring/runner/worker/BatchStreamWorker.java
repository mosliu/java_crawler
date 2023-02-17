package net.liuxuan.crawler.spring.runner.worker;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 积攒数据，进行缓存批式化处理的流式处理worker基类
 * @date 2020-08-28
 **/
@Slf4j
public abstract class BatchStreamWorker<S, R> extends StreamWorker<S, R> {
    protected Long lasttime = System.currentTimeMillis();
    /**
     * 最小的Size
     */
    protected Integer batchMinSize = 20;
    /**
     * 最大等待 ,ms
     */
    protected Long reqMaxIdle = 15000L;

    /**
     * 内部缓冲区，要考虑block情况
     */
    protected LinkedBlockingQueue<S> databuffer = new LinkedBlockingQueue<>(1000);


    @Override
    public void run() {

        /**
         * 预先准备
         */
        prepareRun();
        log.info("{} starting ，thread：{}", this.getClass().getName(), this.getName());
        while (!isInterrupted()) {
            try {
                while (runFlag) {
                    if (inBlockQueue == null && databuffer.size() == 0) {
                        setRunFlag(false);
                    } else {
                        S take = getCurrentMsg();

                        if (take != null || (databuffer.size() > 0 && System.currentTimeMillis() - lasttime > reqMaxIdle)) {
                            //take可能为null
                            R processed = processMessage(take);
                            //上一步必须返回null
                            if (processed != null) {
                                log.warn("Batch处理中，理论上必须返回null，应检查");
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
                log.error("程序调度异常,处理时异常发生，线程可能会死掉！需要检查！", ex);
//                logError("程序调度异常", "处理时异常发生，线程可能会死掉！需要检查！", ex);
            }
        }
    }


    /**
     * proceessMessage 通用的处理过程，重载后应该重新调一遍，
     * 批量处理中，将获取放置到处理中。需要考虑获取时阻塞。
     * 必须返回null
     * 当前设计，检查应该发送且buffer有数据时，先进行发送，然后再获取
     *
     * @param msg
     * @return
     */
    @Override
    public R processMessage(S msg) {
        //来数据后放入缓冲队列
        Long now = System.currentTimeMillis();
        try {
            if (msg == null) {
//                if (stopStreamFlag) {
//                } else {
//                }
            } else {
                databuffer.put(msg);
            }

            if (databuffer.size() >= batchMinSize || now - lasttime > reqMaxIdle) {
                //更新最后请求时间
                lasttime = System.currentTimeMillis();

                LinkedList<S> srcList = new LinkedList<>();
                //从databuffer中获取所有数据
                int count = databuffer.drainTo(srcList);
                if (count < 1) {
                    return null;
                }
                prepareSrcList(srcList);

                List<R> batchList = convertSrcListToBatchList(srcList);

                doBatch(batchList);

            }

        } catch (InterruptedException e) {
//            log.error("error", e);
            log.info("{} : {} 中断，退出", this.getClass().getName(), this.getName());
        }
//        return msg;
        return null;

    }

    /**
     * 获取数据，默认是从inQueue中获取的，无则等待100ms，再无则返回null
     * 可重载
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    protected S getCurrentMsg() throws InterruptedException {

        if (stopStreamFlag && inBlockQueue.size() == 0) {
            //放一个进入队列，放置批处理时卡住
//            TagBigDataMessage ticktock = new TagBigDataMessage();
//            return ticktock;
            return null;
        }

        if (inBlockQueue.size() == 0 && databuffer.size() > 0 && System.currentTimeMillis() - lasttime > reqMaxIdle) {
            return null;
        }


//        return inBlockQueue.take();
        return inBlockQueue.poll(100, TimeUnit.MILLISECONDS);
    }


    /**
     * 针对获取的数据进行预处理，默认无处理，可以覆盖该函数
     *
     * @param srcList
     * @throws InterruptedException
     */
    protected void prepareSrcList(List<S> srcList) throws InterruptedException {

    }

    /**
     * 将预处理好的队列，转换为准备送入批处理的队列
     *
     * @param srcList
     * @return
     * @throws InterruptedException
     */
    protected List<R> convertSrcListToBatchList(List<S> srcList) throws InterruptedException {
        return srcList.parallelStream().map(this::s2r).collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * 做转换，将传入的数据类型由原始数据类型改为新的类型
     *
     * @param msg
     * @return
     */
    protected abstract R s2r(S msg);

    /**
     * 针对新的类型数据做批量处理
     * 处理后的数据会送入下一流程
     *
     * @param bdList
     * @throws InterruptedException
     */
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
     * 批量的数据做处理，返回的list会进入下一步，抽象方法，需实现
     * 可以返回null，则不向下处理
     *
     * @param bdList
     * @return
     */
    protected abstract List<R> doBatchProcess(List<R> bdList);

    /**
     * 获取内部buffer当前缓冲数据量
     *
     * @return
     */
    public int getInnerBufferSize() {
        return databuffer == null ? 0 : databuffer.size();
    }

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


    @Override
    public boolean stopMe() {
        //防止停止时没有触发，卡在批处理中
        processMessage(null);
        return super.stopMe();
    }
}
