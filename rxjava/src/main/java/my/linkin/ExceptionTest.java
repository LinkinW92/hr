package my.linkin;

import java.util.concurrent.ExecutionException;

public class ExceptionTest {
    public static void main(String[] args) {
        try {
            throw new RuntimeException();
        }catch (BizException e) {
            System.out.println("22222");
            throw e;
        }finally {
            System.out.println("1111");
        }
    }
}
