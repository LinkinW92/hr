package my.linkin;

import java.util.List;

public class Test1 {
        public String method(String list){
            System.out.println("List String");
            return "";
        }
        public boolean method(List<Integer> list){
            System.out.println("List Int");
            return true;
        }

    public static void main(String[] args) {
        ThreadLocal<Integer> threadLocal1 = new ThreadLocal<>();
        threadLocal1.set(1);
        threadLocal1 = null;
        System.gc();
        ThreadLocal<String> threadLocal2 = new ThreadLocal<>();
        threadLocal2.set("111");
        threadLocal1 = new ThreadLocal<>();
        threadLocal1.set(2);
        Thread t = Thread.currentThread();
        System.out.println("hahhaa");
        System.out.println(0x61c88647);
        System.out.println(0x61c88647 + 0x61c88647);
    }
}
