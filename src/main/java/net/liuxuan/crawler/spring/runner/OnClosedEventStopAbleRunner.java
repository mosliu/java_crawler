package net.liuxuan.crawler.spring.runner;

import lombok.extern.slf4j.Slf4j;
import net.liuxuan.crawler.spring.SpringContext;
import net.liuxuan.crawler.spring.runner.worker.*;
import net.liuxuan.crawler.utils.tuple.Tuple2;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description Runner虚基类，可以接受停止信号，逐步停止。
 * @date 2019-04-17
 **/
@Slf4j
public abstract class OnClosedEventStopAbleRunner implements ApplicationRunner, ApplicationListener<ContextClosedEvent> {


    protected ThreadPoolTaskExecutor runnerExecutor;

    private String name = "";

    protected List<StopAbleThread> stopAbleThreads = new ArrayList<>();
    protected List<Tuple2<CountDownLatch, List<StopAbleThread>>> stopAbleThreadsWithCountDownLatch = new ArrayList<>();


    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        log.warn("{} shutdown signal received", name);

        stopAbleThreadsWithCountDownLatch.forEach(tuple -> {
            CountDownLatch countdown = tuple.getV1();
            List<StopAbleThread> threads = tuple.getV2();
            threads.parallelStream().forEach(sap -> {
                while (!sap.stopMe()) {
                    log.info("send stop signal to {},class:{}", sap.getName(), sap.getClass().getName());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        log.info("进程终止：{}", this.getName());
                    }
                }
                log.info("Stopping {},class:{}", sap.getName(), sap.getClass().getName());
                sap.interrupt();
                try {
                    sap.join();
                } catch (InterruptedException e) {
                    log.info("进程终止：{}", this.getName());
                }
            });
            if (countdown.getCount() != 0) {
                log.info("！！！！！有进程未调用countdown！！！，{},class:{}", threads.get(0).getName(), threads.get(0).getClass().getName());
            }
            try {
                countdown.await(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            try {
//                countdown.await();
//            } catch (InterruptedException e) {
//                log.info("进程终止：{}", this.getName());
//            }
        });


//        stopAbleThreads.forEach(ele -> {
//            while (!ele.stopMe()) {
//                log.info("send stop signal to {}", ele.getName());
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    log.info("进程终止：{}", this.getName());
//                }
//            }
//            log.info("Stopping {},class:{}", ele.getName(), ele.getClass().getName());
//            ele.interrupt();
//            try {
//                ele.join();
//            } catch (InterruptedException e) {
//                log.info("进程终止：{}", this.getName());
//            }
//        });
        log.warn("{} Stopped", name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ThreadPoolTaskExecutor getRunnerExecutor() {
        return runnerExecutor;
    }

    /**
     * 启动线程，自动修改核心线程数
     *
     * @param clz   Class<? extends StopAbleThread>
     * @param count 数量
     */
    protected void startThread(Class<? extends StopAbleThread> clz, int count) {

        List<StopAbleThread> newGroupThread = new ArrayList<>(count);
        CountDownLatch groupCountDown = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            StopAbleThread c = (StopAbleThread) SpringContext.getBean(clz);
            runnerExecutor.execute(c);
            stopAbleThreads.add(c);
            c.setThreadStop(groupCountDown);
            newGroupThread.add(c);
        }
        stopAbleThreadsWithCountDownLatch.add(new Tuple2<>(groupCountDown, newGroupThread));
        runnerExecutor.setCorePoolSize(runnerExecutor.getCorePoolSize() + count);
    }

    protected final <C> void startThread(Class<? extends ArgsStopAbleThread<C>> clz, int count, C config) {

        List<StopAbleThread> newGroupThread = new ArrayList<>(count);
        CountDownLatch groupCountDown = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            ArgsStopAbleThread c = (ArgsStopAbleThread) SpringContext.getBean(clz);
            try {
                c.initArgs(config);
            } catch (NoArgsException ex) {
                log.error("初始化ArgsStopAbleThread，但是没有传入参数,启动终止！！。若为无参类型，可使用StreamWorker.", ex);
                return;
            } catch (Exception ex) {
                log.error("初始化ArgsStream但是初始化参数时发生错误！.", ex);
                return;
            }
            runnerExecutor.execute(c);
            stopAbleThreads.add(c);
            c.setThreadStop(groupCountDown);
            newGroupThread.add(c);
        }
        stopAbleThreadsWithCountDownLatch.add(new Tuple2<>(groupCountDown, newGroupThread));
        runnerExecutor.setCorePoolSize(runnerExecutor.getCorePoolSize() + count);
    }

//    /**
//     * 启动线程，自动修改核心线程数
//     *
//     * @param clz Class<? extends StopAbleThread>
//     * @param count 数量
//     */
//    protected void startStopAbleThread(Class<? extends StopAbleThread> clz, int count) {
//
//        List<StopAbleThread> newGroupThread = new ArrayList<>(count);
//        CountDownLatch groupCountDown = new CountDownLatch(count);
//        for (int i = 0; i < count; i++) {
//            StopAbleThread c = SpringContext.getBean(clz);
//            runnerExecutor.execute(c);
//            stopAbleThreads.add(c);
//            c.setThreadStop(groupCountDown);
//            newGroupThread.add(c);
//        }
//        stopAbleThreadsWithCountDownLatch.add(new Tuple2<>(groupCountDown, newGroupThread));
//        runnerExecutor.setCorePoolSize(runnerExecutor.getCorePoolSize() + count);
//    }

    @SafeVarargs
    protected final <S, R, C> void startArgsStream(Boolean enable, Class<? extends StreamWorker<S, R>> clz, int count, C config, LinkedBlockingQueue<S> in, LinkedBlockingQueue<R>... out) {
        if (enable) {
            startArgsStream(clz, count, config, in, out);
        } else {
//            startStream(FakeStreamWorker.class, 1, in, out);
            startFakeStream(in, out);
        }
    }

    @SafeVarargs
    protected final <S, R, C> void startArgsBatchStream(Boolean enable, Class<? extends StreamWorker<S, R>> clz, int count, C config, LinkedBlockingQueue<S> in, LinkedBlockingQueue<R>... out) {
        if (enable) {
            startArgsBatchStream(clz, count, config, in, out);
        } else {
//            startStream(FakeStreamWorker.class, 1, in, out);
            startFakeStream(in, out);
        }
    }

    /**
     * 启动流式worker线程，自动修改核心线程数
     *
     * @param clz    类
     * @param count  数量
     * @param config 配置
     * @param in     输入流
     * @param out    输出流s
     * @param <S>    输入流类型
     * @param <R>    输出流类型
     * @param <C>    配置类型
     */
//    protected void startStream(Class clz, int count, LinkedBlockingQueue<BigDataMessage> in, LinkedBlockingQueue<BigDataMessage>... out) {
    @SafeVarargs
    protected final <S, R, C> void startArgsStream(Class<? extends StreamWorker<S, R>> clz, int count, C config, LinkedBlockingQueue<S> in, LinkedBlockingQueue<R>... out) {
        List<StopAbleThread> newGroupThread = new ArrayList<>(count);
        CountDownLatch groupCountDown = new CountDownLatch(count);

        String className = clz.getName().substring(clz.getName().lastIndexOf('.'));
        runnerExecutor.setThreadGroupName(className);
//        runnerExecutor.setThreadNamePrefix(className);
        for (int i = 0; i < count; i++) {
            ArgsStreamWorker<S, R, C> streamWorker = (ArgsStreamWorker<S, R, C>) SpringContext.getBean(clz);
            try {
                streamWorker.initArgs(config);
            } catch (NoArgsException ex) {
                log.error("初始化ArgsStream，但是没有传入参数,启动终止！！。若为无参类型，可使用StreamWorker.", ex);
                return;
            } catch (Exception ex) {
                log.error("初始化ArgsStream但是初始化参数时发生错误！.", ex);
                return;
            }
            presetAndRunStreamWorker(streamWorker, className + i, in, out);

            streamWorker.setThreadStop(groupCountDown);
            newGroupThread.add(streamWorker);

        }
        stopAbleThreadsWithCountDownLatch.add(new Tuple2<>(groupCountDown, newGroupThread));
        runnerExecutor.setCorePoolSize(runnerExecutor.getCorePoolSize() + count);
    }

    @SafeVarargs
    protected final <S, R, C> void startArgsBatchStream(Class<? extends StreamWorker<S, R>> clz, int count, C config, LinkedBlockingQueue<S> in, LinkedBlockingQueue<R>... out) {
        List<StopAbleThread> newGroupThread = new ArrayList<>(count);
        CountDownLatch groupCountDown = new CountDownLatch(count);

        String className = clz.getName().substring(clz.getName().lastIndexOf('.'));
        runnerExecutor.setThreadGroupName(className);
//        runnerExecutor.setThreadNamePrefix(className);
        for (int i = 0; i < count; i++) {
            ArgsBatchStreamWorker<S, R, C> streamWorker = (ArgsBatchStreamWorker<S, R, C>) SpringContext.getBean(clz);
            try {
                streamWorker.initArgs(config);
            } catch (NoArgsException ex) {
                log.error("初始化ArgsBatchStreamWorker，但是没有传入参数,启动终止！！。若为无参类型，可使用StreamWorker.", ex);
                return;
            } catch (Exception ex) {
                log.error("初始化ArgsBatchStreamWorker 但是初始化参数时发生错误！.", ex);
                return;
            }
            presetAndRunStreamWorker(streamWorker, className + i, in, out);

            streamWorker.setThreadStop(groupCountDown);
            newGroupThread.add(streamWorker);

        }
        stopAbleThreadsWithCountDownLatch.add(new Tuple2<>(groupCountDown, newGroupThread));
        runnerExecutor.setCorePoolSize(runnerExecutor.getCorePoolSize() + count);
    }

    @SafeVarargs
    protected final <S, R> void startStream(Boolean enable, Class<? extends StreamWorker<S, R>> clz, int count, BlockingQueue<S> in, BlockingQueue<R>... out) {
        if (enable) {
            startStream(clz, count, in, out);
        } else {
//            Type[] actualTypeArguments = ((ParameterizedTypeImpl) clz.getGenericSuperclass()).getActualTypeArguments();
            Type[] actualTypeArguments = ((ParameterizedType) clz.getGenericSuperclass()).getActualTypeArguments();
            //这里需要处理S和R不一致时fakeStream不正确的问题，之前没有考虑是因为主要的xg处理里面全是BigDataMessage、
            if (actualTypeArguments.length == 2 && actualTypeArguments[0].getTypeName().equals(actualTypeArguments[1].getTypeName())) {
                startFakeStream(in, out);
            } else {
                log.error("传入的in和out类型不一致，不能启用FakeStream,进入的数据进行了抛弃");
                startFakeStream(in, null);
            }


        }
    }

    @SafeVarargs
    protected final <S, R> void startFakeStream(BlockingQueue<S> in, BlockingQueue<R>... out) {
//        BlockingQueue<S>[] objects = null;
        if (out != null) {
//            objects = (BlockingQueue<S>[]) Arrays.stream(out).map(o -> (BlockingQueue<S>) o).toArray();
        }

        Class<FakeStreamWorker> clz = FakeStreamWorker.class;
        int count = 1;

        List<StopAbleThread> newGroupThread = new ArrayList<>(count);
        CountDownLatch groupCountDown = new CountDownLatch(count);
        String className = clz.getName().substring(clz.getName().lastIndexOf('.'));
        runnerExecutor.setThreadGroupName(className);
//        runnerExecutor.setThreadNamePrefix(className);
        for (int i = 0; i < count; i++) {
            FakeStreamWorker streamWorker = SpringContext.getBean(FakeStreamWorker.class);
            presetAndRunStreamWorker(streamWorker, className + i, in, out);
            streamWorker.setThreadStop(groupCountDown);
            newGroupThread.add(streamWorker);

        }
        runnerExecutor.setCorePoolSize(runnerExecutor.getCorePoolSize() + count);
        stopAbleThreadsWithCountDownLatch.add(new Tuple2<>(groupCountDown, newGroupThread));
    }


    /**
     * 启动流式worker线程，自动修改核心线程数
     *
     * @param clz
     * @param count
     */
//    protected void startStream(Class clz, int count, LinkedBlockingQueue<BigDataMessage> in, LinkedBlockingQueue<BigDataMessage>... out) {
    @SafeVarargs
    protected final <S, R> void startStream(Class<? extends StreamWorker<S, R>> clz, int count, BlockingQueue<S> in, BlockingQueue<R>... out) {
        List<StopAbleThread> newGroupThread = new ArrayList<>(count);
        CountDownLatch groupCountDown = new CountDownLatch(count);

        String className = clz.getName().substring(clz.getName().lastIndexOf('.'));
        runnerExecutor.setThreadGroupName(className);
//        runnerExecutor.setThreadNamePrefix(className);
        for (int i = 0; i < count; i++) {
            StreamWorker<S, R> streamWorker = (StreamWorker<S, R>) SpringContext.getBean(clz);

            presetAndRunStreamWorker(streamWorker, className + i, in, out);

            streamWorker.setThreadStop(groupCountDown);
            newGroupThread.add(streamWorker);

        }
        runnerExecutor.setCorePoolSize(runnerExecutor.getCorePoolSize() + count);
        stopAbleThreadsWithCountDownLatch.add(new Tuple2<>(groupCountDown, newGroupThread));
    }

    /**
     * 预设定StreamWorker，设定名称，输入流和输出流s
     *
     * @param streamWorker
     * @param thread_name
     * @param in
     * @param out
     */
    private <S, R> void presetAndRunStreamWorker(StreamWorker<S, R> streamWorker, String thread_name, BlockingQueue<S> in, BlockingQueue<R>[] out) {
        streamWorker.setName(thread_name);
        if (in == null)
            log.warn("thread_name:{},in queue is null!!!", thread_name);
        streamWorker.setInBlockQueue(in);
        if (out != null) {
            for (int j = 0; j < out.length; j++) {
                if (out[j] != null)
                    streamWorker.addOutBlockQueue(out[j]);
            }
        }
        runnerExecutor.execute(streamWorker);
        stopAbleThreads.add(streamWorker);
    }


    /**
     * 启动单个StopAbleThread
     * 适用于用了单独配置的BigDataStaticsCounter
     * 启动时会加入结束时终止列表
     * 启动时会自动增加运行的核心池
     *
     * @param task StopAbleThreadTask
     */
    protected void execute(StopAbleThread task) {
        List<StopAbleThread> newGroupThread = new ArrayList<>(1);
        CountDownLatch groupCountDown = new CountDownLatch(1);

        runnerExecutor.execute(task);
        stopAbleThreads.add(task);
        runnerExecutor.setCorePoolSize(runnerExecutor.getCorePoolSize() + 1);

        task.setThreadStop(groupCountDown);
        newGroupThread.add(task);
        stopAbleThreadsWithCountDownLatch.add(new Tuple2<>(groupCountDown, newGroupThread));
    }

    public abstract void setRunnerExecutor(ThreadPoolTaskExecutor runnerExecutor);

}
