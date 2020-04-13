package my.linkin.thread;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: chunhui.wu
 * @Date: 2019/8/14 00:04
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CenterConfig {
    private Integer consumers = 1;//消费者数量
    private Integer producers = 1;//生产者数量
    private Integer queueSize = 1000;//阻塞队列大小
    private Integer consumeTimeout = 500;//ms  消费者poll时阻塞时间

    public Integer getPoolSize() {
        return this.consumers + this.producers;
    }
}
