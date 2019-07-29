package my.linkin.endpoint;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/hystrix", consumes = MediaType.ALL_VALUE)
public class HystrixEndpoint {


    @HystrixCommand(commandKey = "ht1", commandProperties = {@HystrixProperty(name = "execution.isolation.strategy", value = "THREAD")},
    threadPoolKey = "pool-th1", threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "10"),
            @HystrixProperty(name = "maxQueueSize", value = "2000"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "30")
    },
    fallbackMethod = "helloFallback")
    @GetMapping("/hello/{name}")
    public String t1(@PathVariable String name) {
        try {
            Thread.sleep(888);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Hello ".concat(name);
    }

    public String helloFallback(@PathVariable String name) {
        return "Hello Fallback" + name;
    }
}
