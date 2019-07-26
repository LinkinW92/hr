package my.linkin.hystrix.service.impl;

import my.linkin.hystrix.service.IOrderService;
import org.springframework.stereotype.Service;


@Service
public class OrderServiceImpl implements IOrderService {


    @Override
    public Integer count() {
        Integer seed = (int) (Math.random() * 10);
        if (seed > 5) {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                //TODO
            }
        }
        return 1;
    }
}
