package com.github.tatercertified.potatoptimize.mixin.threading.paletted_container;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.world.chunk.BiMapPalette;
import net.minecraft.world.chunk.PaletteResizeListener;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.ReadableContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.LongStream;

@Mixin(PalettedContainer.class)
public abstract class SyncPalettedContainerMixin<T> implements PaletteResizeListener<T>,
        ReadableContainer<T>  {
    @Shadow private volatile PalettedContainer.Data<T> data;

    @Shadow protected abstract PalettedContainer.Data<T> getCompatibleData(PalettedContainer.@Nullable Data<T> previousData, int bits);

    @Shadow protected abstract T swap(int index, T value);

    @Shadow @Final private PalettedContainer.PaletteProvider paletteProvider;

    @Shadow protected abstract void set(int index, T value);

    @Shadow @Final private PaletteResizeListener<T> dummyListener;

    /**
     * @author QPCrummer
     * @reason Synchronize instead
     */
    @Overwrite
    public void unlock() {
    }

    /**
     * @author QPCrummer
     * @reason Synchronize instead
     */
    @Overwrite
    public void lock() {
    }

    /**
     * @author QPCrummer
     * @reason Synchronize instead
     */
    @Overwrite
    public int onResize(int i, T object) {
       return synchronizedOnResize(i, object);
    }

    /**
     * @author QPCrummer
     * @reason Synchronize instead
     */
    @Overwrite
    public Object swap(int x, int y, int z, T value) {
        return synchronizeSwap(x, y, z, value);
    }

    /**
     * @author QPCrummer
     * @reason Synchronize instead
     */
    @Overwrite
    public void set(int x, int y, int z, T value) {
        synchronizedSet(x, y, z, value);
    }

    /**
     * @author QPCrummer
     * @reason Synchronize instead
     */
    @Overwrite
    public void readPacket(PacketByteBuf buf) {
        synchronizedRead(buf);
    }

    /**
     * @author QPCrummer
     * @reason Synchronize instead
     */
    @Overwrite
    public void writePacket(PacketByteBuf buf) {
        synchronizedWrite(buf);
    }

    /**
     * @author QPCrummer
     * @reason Synchronize instead
     */
    @Overwrite
    public ReadableContainer.Serialized<T> serialize(IndexedIterable<T> idList, PalettedContainer.PaletteProvider paletteProvider) {
        return synchronizedSerialize(idList, paletteProvider);
    }

    @Unique
    private synchronized int synchronizedOnResize(int i, T object) {
        PalettedContainer.Data<T> data = this.data;
        PalettedContainer.Data<T> data2 = this.getCompatibleData(data, i);
        data2.importFrom(data.palette, data.storage);
        this.data = data2;
        return data2.palette.index(object);
    }

    @Unique
    private synchronized Object synchronizeSwap(int x, int y, int z, T value) {
        return this.swap(this.paletteProvider.computeIndex(x, y, z), value);
    }

    @Unique
    private synchronized void synchronizedSet(int x, int y, int z, T value) {
        this.set(this.paletteProvider.computeIndex(x, y, z), value);
    }

    @Unique
    private synchronized void synchronizedRead(PacketByteBuf buf) {
        byte i = buf.readByte();
        PalettedContainer.Data<T> data = this.getCompatibleData(this.data, i);
        data.palette.readPacket(buf);
        buf.readLongArray(data.storage.getData());
        this.data = data;
    }

    @Unique
    private synchronized void synchronizedWrite(PacketByteBuf buf) {
        this.data.writePacket(buf);
    }
    
    @Unique
    private synchronized ReadableContainer.Serialized<T> synchronizedSerialize(IndexedIterable<T> idList, PalettedContainer.PaletteProvider paletteProvider) {
        Optional<LongStream> optional;
        BiMapPalette<T> biMapPalette = new BiMapPalette<>(idList, this.data.storage.getElementBits(), this.dummyListener);
        int i = paletteProvider.getContainerSize();
        int[] is = new int[i];
        this.data.storage.method_39892(is);
        PalettedContainer.applyEach(is, id -> biMapPalette.index(this.data.palette.get(id)));
        int j = paletteProvider.getBits(idList, biMapPalette.getSize());
        if (j != 0) {
            PackedIntegerArray packedIntegerArray = new PackedIntegerArray(j, i, is);
            optional = Optional.of(Arrays.stream(packedIntegerArray.getData()));
        } else {
            optional = Optional.empty();
        }
        return new ReadableContainer.Serialized<>(biMapPalette.getElements(), optional);
    }
}
