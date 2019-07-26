package my.linkin.hystrix;


import my.linkin.hystrix.command.OrderCountCommand;
import my.linkin.hystrix.service.IOrderService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderCountCommandTest extends AbstractBaseTests {

    @Autowired
    private IOrderService service;

    @Test
    public Integer orderCount() {
        return new OrderCountCommand(service).execute();
    }

}
