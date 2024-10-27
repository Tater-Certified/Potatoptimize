package com.github.tatercertified.potatoptimize.mixin.world.saving;

import com.github.tatercertified.potatoptimize.mixin.logic.main_thread.ExceptionHandlerInvoker;
import com.github.tatercertified.potatoptimize.utils.interfaces.AsyncChunkSaveInterface;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Util;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

// Credit to PaperMC PR #10171
@IfModAbsent(value = "c2me")
@Mixin(PersistentState.class)
public abstract class DataIOSavingMixin implements AsyncChunkSaveInterface {

    @Shadow public abstract NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);

    @Shadow public abstract boolean isDirty();

    @Shadow public abstract void setDirty(boolean dirty);


    @Override
    public void saveAsync(Path file, RegistryWrapper.WrapperLookup registryLookup, @Nullable ExecutorService ioExecutor) {
        if (this.isDirty()) {
            NbtCompound compoundTag = new NbtCompound();
            compoundTag.put("data", this.writeNbt(new NbtCompound(), registryLookup));
            NbtHelper.putDataVersion(compoundTag);

            Runnable writeRunnable = () -> {
                try {
                    NbtIo.writeCompressed(compoundTag, file);
                } catch (IOException var4) {
                    ExceptionHandlerInvoker.getLogger().error("Could not save data {}", this, var4);
                }
            };

            if (ioExecutor == null) {
                Util.getIoWorkerExecutor().execute(writeRunnable);
            } else {
                ioExecutor.execute(writeRunnable);
            }


            this.setDirty(false);
        }
    }


}
