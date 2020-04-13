package my.linkin;

import org.junit.Test;

import java.util.Locale;

public class  Intern {

    @SuppressWarnings("redundant")
    public static void main(String[] args) {
        String s = new String("1");
        s.intern();
        String s2 = "1";
        System.out.println(s == s2);

        String s3 = new String("1") + new String("1");
        s3.intern();
        String s4 = "11";
        System.out.println(s3 == s4);

        System.out.println("route" +System.getProperty("jvmRoute"));
    }

    @Test
    public void ctl() {
        Integer mask = (1 << 29) - 1;
        System.out.println(mask);
        System.out.println(~mask);
    }
}
