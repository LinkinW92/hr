package my.linkin.hystrix.command;

import com.netflix.hystrix.*;
import my.linkin.hystrix.service.IOrderService;


public class OrderCountCommand extends HystrixCommand<Integer> {

    private IOrderService orderService;

    private static final String ORDER_CK = "ORDER";

    public OrderCountCommand(IOrderService orderService) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(ORDER_CK))
                .andCommandKey(HystrixCommandKey.Factory.asKey("orderCount"))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withCircuitBreakerEnabled(true)
                                .withCircuitBreakerRequestVolumeThreshold(20)
                                .withCircuitBreakerSleepWindowInMilliseconds(1000)
                                .withCircuitBreakerErrorThresholdPercentage(50)
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                                .withExecutionIsolationThreadInterruptOnFutureCancel(true)
                                .withExecutionIsolationThreadInterruptOnTimeout(true)
                                .withExecutionTimeoutInMilliseconds(1000)
                                .withFallbackEnabled(true)
                ).andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(10).withMaximumSize(10)));
    }

    @Override
    protected Integer run() throws Exception {
        return orderService.count();
    }

    @Override
    protected Integer getFallback() {
        return -1;
    }
}
