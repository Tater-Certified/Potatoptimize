package com.github.tatercertified.potatoptimize.mixin.world.saving;

import com.github.tatercertified.potatoptimize.utils.interfaces.AsyncChunkManagerInterface;
import com.github.tatercertified.potatoptimize.utils.interfaces.AsyncChunkSaveInterface;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Credit to PaperMC PR #10171
@IfModAbsent(value = "c2me")
@Mixin(PersistentStateManager.class)
public abstract class PersistentStateManagerMixin implements AsyncChunkManagerInterface {
    @Mutable
    @Final
    private ExecutorService ioExecutor;
    @Shadow @Final private Map<String, PersistentState> loadedStates;

    @Shadow @Final private Path directory;

    @Shadow public abstract void save();

    @Shadow protected abstract Path getFile(String id);

    @Shadow @Final private RegistryWrapper.WrapperLookup registries;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void assignIOExecutor(CallbackInfo ci) {
        this.ioExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("PotatoptimizeAsyncIO - " + this.directory + " - %d").setDaemon(true).build());
    }


    @Override
    public void saveAsync() {
        this.loadedStates.forEach((id, state) -> {
            if (state != null) {
                ((AsyncChunkSaveInterface)state).saveAsync(this.getFile(id), this.registries, this.ioExecutor);
            }
        });
    }

    @Override
    public void close() {
        this.save();
    }
}
