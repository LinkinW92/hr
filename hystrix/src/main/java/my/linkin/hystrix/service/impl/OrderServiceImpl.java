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

    public static void main(String[] args) {
        int i = Integer.MAX_VALUE + 1;
        System.out.println(i);
        System.out.println(i - 2);
        System.out.println(7 >> 6);
        System.out.println(128 >> 6);
        System.out.println(32 >> 6);
        System.out.println(63 >> 6);
    }
}
