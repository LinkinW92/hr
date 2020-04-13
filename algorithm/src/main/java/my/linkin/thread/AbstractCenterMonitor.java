package my.linkin.thread;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import my.linkin.common.BizException;
import my.linkin.common.ResultCode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: chunhui.wu
 * @Date: 2019/8/12 17:43
 * @Description:
 */
@Data
@Slf4j
public abstract class AbstractCenterMonitor<E> {

    private AtomicInteger produced = new AtomicInteger(0);//生产者生产总数
    private AtomicInteger consumed = new AtomicInteger(0);//消费者消费总数
    private AtomicInteger succeed = new AtomicInteger(0);//消费成功数
    private AtomicInteger failed = new AtomicInteger(0);//消费失败数
    private Long batchNo;//导入批次号,最为memberPrize的sourceId字段传入
    private String fileName;//导入文件名称
    private String importer;//导入人
    private Throwable thr;
    private AtomicBoolean canStopConsumer;//用来标识消费者是否可关闭(生产者完全关闭后,该标识置为true)

    private List<E> list = new ArrayList<>();//失败记录

    public AbstractCenterMonitor(String fileName, String importer) {
        this.batchNo = System.currentTimeMillis();
        try {
            this.fileName = new String(fileName.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            log.error("UnsupportedEncodingException");
            this.fileName = fileName;
        }
        this.importer = importer;
        this.canStopConsumer = new AtomicBoolean(false);
    }

    public void produce() {
        this.produced.incrementAndGet();
    }

    public void consume() {
        this.consumed.incrementAndGet();
    }

    public void succeed() {
        this.succeed.incrementAndGet();
    }

    public Integer getProduced() {
        return this.produced.get();
    }

    public Integer getConsumed() {
        return this.consumed.get();
    }

    public Integer getSucceed() {
        return this.succeed.get();
    }

    public Integer getFailed() {
        return this.failed.get();
    }

    public boolean canStopConsumer() {
        return this.canStopConsumer.get();
    }

    public void stopConsumer() {
        this.canStopConsumer.set(true);
    }

    public void fail(E e) {
        this.failed.incrementAndGet();
        this.list.add(e);
    }

    public void onError(Throwable t) {
        this.thr = t;
    }

    public void tryError() {
        if (this.thr != null) {
            throw new BizException(ResultCode.BIZ_EXCEPTION, thr.getMessage());
        }
    }

    /*
     * 清除数据, 业务流程执行后,进行monitor清理
     * */
    public abstract void clear();

}
