package my.linkin.gc;

import java.util.ArrayList;
import java.util.List;

public class GCDemo {

    private static final int _1MB= 1 * 1024 * 1024;
    private static final int _HMB= 1 * 1024 * 1024 / 4;

    /**
     * -Xms20m -Xmm20m -Xmn10m -XX:+PrintGCDetails -XX:-PrintGCTimestamps
     *
     *
     * */
    public static void main(String[] args) {
        List<Chunk> chunks = new ArrayList<>();
        while (true) {
            chunks.add(new Chunk(_HMB));
        }

    }
}

class Chunk {
    private byte[] chunk;

    public Chunk(int size) {
        this.chunk = new byte[size];
    }
}
