package my.linkin.thread;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * @Auther: chunhui.wu
 * @Date: 2019/8/12 10:17
 * @Description:
 */
@Slf4j
public class DataProcessCenter<E> {

    private BlockingQueue<E> queue;
    private ExecutorService executor;
    private AbstractCenterMonitor abstractCenterMonitor;
    private CountDownLatch latch;
    private Runnable callback;
    private CenterConfig config;

    /**
     * @param monitor  生产者消费者监控数据
     * @param callback 线程池关闭后要执行的操作
     * @param config   消费者生产者配置
     */
    public DataProcessCenter(AbstractCenterMonitor monitor, CenterConfig config, Runnable callback) {
        this.queue = new LinkedBlockingQueue<>(config.getQueueSize());
        this.executor = Executors.newFixedThreadPool(config.getPoolSize(), new BasicThreadFactory.Builder()
                .namingPattern("data-center-%d")
                .uncaughtExceptionHandler((t, e) -> {
                    log.error("线程执行异常:{}", e);
                    t.interrupt();
                    onError(e);
                }).build());
        this.abstractCenterMonitor = monitor;
        this.latch = new CountDownLatch(config.getPoolSize());
        this.callback = callback;
        this.config = config;
    }

    public void close() {
        try {
            if (this.callback != null) this.callback.run();
        } catch (Exception e) {
            log.error("回调程序异常:{}", e);
        } finally {
            this.executor.shutdown();
            log.info("线程池关闭...");
        }
    }

    public void countDown() {
        synchronized (this.latch) {
            this.latch.countDown();
            log.info("当前剩余线程数:{}", this.latch.getCount());
            if (this.latch.getCount() == 0) {
                close();
            }
        }
    }

    public BlockingQueue getQueue() {
        return queue;
    }

    public void addProducer(Runnable r) {
        this.executor.submit(r);
    }

    public void addConsumer(Runnable r) {
        this.executor.submit(r);
    }


    /*
     * 设置canStopConsumer标志位,避免消费者比生产者早启动或生产速度过慢,超时时间内未消费到元素而过早关闭。
     * */
    public E poll() throws InterruptedException {
        return this.queue.poll(config.getConsumeTimeout(), TimeUnit.MILLISECONDS);
    }

    /*
     * 中断异常由生产者处理
     * */
    public void produce(E e) throws InterruptedException {
        this.queue.put(e);
        this.abstractCenterMonitor.produce();
    }

    /*
     * 业务程序不直接访问monitor的计数方法,由该类提供统一入口
     * */
    public void consume() {
        this.abstractCenterMonitor.consume();
    }

    public void succeed() {
        this.abstractCenterMonitor.succeed();
    }

    public void fail(E e) {
        this.abstractCenterMonitor.fail(e);
    }


    /*
     * 阻塞方法,阻塞到latch 值为0
     * */
    public AbstractCenterMonitor getBlockingMonitor() {
        try {
            while (!this.executor.isShutdown()) {
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            log.error("error occurs when monitor blocking:{}", e);
            Thread.currentThread().interrupt();
        }
        return this.abstractCenterMonitor;
    }

    public AbstractCenterMonitor getMonitor() {
        return this.abstractCenterMonitor;
    }

    public void onError(Throwable t) {
        this.abstractCenterMonitor.onError(t);
    }

    public void stopConsumer() {
        this.abstractCenterMonitor.stopConsumer();
    }

    public boolean canStopConsumer() {
        return this.abstractCenterMonitor.canStopConsumer();
    }
}
