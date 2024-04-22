package com.github.tatercertified.potatoptimize.utils.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncExplosion {
        public static ExecutorService EXECUTOR;
        public static final Logger LOGGER = LoggerFactory.getLogger("Potatoptimize Explosion Thread");

        public static void initExecutor(boolean fixed, int size) {
            EXECUTOR = fixed ? Executors.newFixedThreadPool(size) : Executors.newCachedThreadPool();
        }

        public static void stopExecutor() {
            if (EXECUTOR != null) {
                try {
                    EXECUTOR.shutdownNow();
                } catch (Exception e) {
                    LOGGER.error("Failed to shutdown Explosion Thread: ", e);
                }
            }
        }
}
