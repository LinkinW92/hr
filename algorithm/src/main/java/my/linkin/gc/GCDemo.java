package my.linkin.gc;

import java.util.ArrayList;
import java.util.List;

public class GCDemo {

    private static final int _1MB= 1 * 1024 * 1024;
    private static final int _HMB= 1 * 1024 * 1024 / 4;

    /**
     * -Xms20m -Xmm20m  -XX:+PrintGCDetails -XX:-PrintGCTimestamps
     * -Xmn10m  G1 应禁止配置新生代大小，在设置的情况下，影响G1的动态调整新生代大小和启发式算法
     * -XX:MaxGCPauseMillis=n 设置一个暂停时间期望目标，这是一个软目标，JVM会近可能的保证这个目标
     * -XX:InitiatingHeapOccupancyPercent=n 内存占用达到整个堆百分之多少的时候开启一个GC周期，G1 GC会根据整个栈的占用，而不是某个代的占用情况去触发一个并发GC周期，0表示一直在GC，默认值是45
     * -XX:NewRatio=n 年轻代和老年代大小的比例，默认是2
     * -XX:SurvivorRatio=n eden和survivor区域空间大小的比例，默认是8
     * -XX:MaxTenuringThreshold=n 晋升的阈值，默认是15（译者注：一个存活对象经历多少次GC周期之后晋升到老年代)
     * -XX:ParallelGCThreads=n GC在并行处理阶段试验多少个线程，默认值和平台有关。（译者注：和程序一起跑的时候，使用多少个线程)
     * -XX:ConcGCThreads=n 并发收集的时候使用多少个线程，默认值和平台有关。（译者注:stop-the-world的时候，并发处理的时候使用多少个线程)
     * -XX:G1ReservePercent=n  预留多少内存，防止晋升失败的情况，默认值是10
     * -XX:G1HeapRegionSize=n G1 GC的堆内存会分割成均匀大小的区域，这个值设置每个划分区域的大小，这个值的默认值是根据堆的大小决定的。最小值是1Mb，最大值是32Mb
     * */
    public static void main(String[] args) {
        List<Chunk> chunks = new ArrayList<>();
        while (true) {
            chunks.add(new Chunk(_1MB));
        }

    }
}

class Chunk {
    private byte[] chunk;

    public Chunk(int size) {
        this.chunk = new byte[size];
    }
}
