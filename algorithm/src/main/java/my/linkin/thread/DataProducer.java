package my.linkin.thread;

public abstract class DataProducer<R> implements Runnable {

    /**
     * @return R
     */
    public abstract R produce();
}


