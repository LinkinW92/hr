//package my.linkin.thread;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * @Auther: chunhui.wu
// * @Date: 2019/8/10 16:17
// * @Description: 消费者线程
// */
//@Slf4j
//public abstract class DataConsumer<T> implements Runnable {
//
//    protected DataProcessCenter<T> center;
//
//    public DataConsumer(DataProcessCenter<T> center) {
//        this.center = center;
//    }
//
//    /**
//     * @param t 消费对象
//     */
//    public abstract void consume(T t);
//
//    @Override
//    public void run() {
//        try {
//            T element;
//            //双层循环避免消费者启动时间早于生产者 或 生产者生产速度过慢 导致消费者超时关闭的问题
//            while (!this.center.canStopConsumer()) {
//                while ((element = this.center.poll()) != null) {
//                    this.center.consume();
//                    this.consume(element);
//                }
//                log.info("消费者超时时间内未消费到元素, 当前canStopConsumer:{}", this.center.canStopConsumer());
//            }
//        } catch (Exception e) {
//            log.error("阻塞队列获取元素异常:{}", e);
//            this.center.onError(e);
//        } finally {
//            this.center.countDown();//超过超时时间未消费到元素或抛出异常 关闭消费者
//        }
//    }
//
//}
