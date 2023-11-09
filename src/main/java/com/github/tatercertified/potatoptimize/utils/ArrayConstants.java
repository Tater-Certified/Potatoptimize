package com.github.tatercertified.potatoptimize.utils;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.world.ServerWorld;

// Credit to Titanium Town

/**
 * This class is used to reduce array allocations and improve memory usage
 */
public final class ArrayConstants {
    private ArrayConstants() {}

    public static final int[] emptyIntArray = new int[0];
    public static final int[] zeroSingletonIntArray = new int[]{0};
    public static final byte[] emptyByteArray = new byte[0];
    public static final String[] emptyStringArray = new String[0];
    public static final long[] emptyLongArray = new long[0];
    public static final EquipmentSlot[] equipmentSlotArray = EquipmentSlot.values();

}
