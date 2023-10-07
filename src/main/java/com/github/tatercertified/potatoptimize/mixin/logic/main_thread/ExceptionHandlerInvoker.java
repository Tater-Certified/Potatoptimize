package com.github.tatercertified.potatoptimize.mixin.logic.main_thread;

import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Util.class)
public interface ExceptionHandlerInvoker {
    @Invoker("uncaughtExceptionHandler")
    static void invokeUncaughtExceptionHandler(Thread thread, Throwable t) {
        throw new AssertionError();
    }

    @Accessor("LOGGER")
    static Logger getLogger() {
        throw new AssertionError();
    }
}
