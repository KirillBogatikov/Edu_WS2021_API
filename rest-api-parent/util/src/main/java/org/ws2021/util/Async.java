package org.ws2021.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Async {
    private static final Executor pool;
    
    static {
        pool = Executors.newFixedThreadPool(32);
    }
    
    public static void run(Runnable r) {
        pool.execute(r);
    }
}
