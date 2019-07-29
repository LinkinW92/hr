package my.linkin;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import org.junit.Test;

public class RxTest {

    @Test
    public void flow() {
        Flowable.just("1", "2").subscribe(System.out::println);
        Flowable.just("Hello world").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
            }
        });
        Flowable.range(0, 5).map(i -> i * 5)
        .filter(i -> i%10 == 0).subscribe(System.out::println);
    }
}
