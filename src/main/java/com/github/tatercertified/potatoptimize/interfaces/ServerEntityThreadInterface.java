package com.github.tatercertified.potatoptimize.interfaces;

import com.github.tatercertified.potatoptimize.utils.threading.ThreadedTaskExecutor;

public interface ServerEntityThreadInterface {
    ThreadedTaskExecutor getEntityExecutor();
}
