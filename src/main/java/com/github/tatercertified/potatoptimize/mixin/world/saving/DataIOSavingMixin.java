package com.github.tatercertified.potatoptimize.mixin.world.saving;

import com.github.tatercertified.potatoptimize.mixin.logic.main_thread.ExceptionHandlerInvoker;
import com.github.tatercertified.potatoptimize.utils.interfaces.AsyncChunkSaveInterface;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import net.minecraft.world.PersistentState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.IOException;

// Credit to Paper PR #9408
@Mixin(PersistentState.class)
public abstract class DataIOSavingMixin implements AsyncChunkSaveInterface {

    @Shadow public abstract NbtCompound writeNbt(NbtCompound nbt);

    @Shadow public abstract boolean isDirty();

    @Shadow public abstract void setDirty(boolean dirty);

    /**
     * @author QPCrummer
     * @reason Make async
     */
    @Overwrite
    public void save(File file) {
        save(file, false);
    }

    @Override
    public void save(File file, boolean async) {
        if (this.isDirty()) {
            NbtCompound compoundTag = new NbtCompound();
            compoundTag.put("data", this.writeNbt(new NbtCompound()));
            NbtHelper.putDataVersion(compoundTag);

            Runnable writeRunnable = () -> {
                try {
                    NbtIo.writeCompressed(compoundTag, file.toPath());
                } catch (IOException var4) {
                    ExceptionHandlerInvoker.getLogger().error("Could not save data {}", this, var4);
                }
            };

            if (async) {
                Util.getIoWorkerExecutor().execute(writeRunnable);
            } else {
                writeRunnable.run();
            }

            this.setDirty(false);
        }
    }
}
