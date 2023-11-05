package com.github.tatercertified.potatoptimize.utils.interfaces;

public interface MergeableItem {
    public static final byte UNCACHED = 0;
    public static final byte CACHED_HALFEMPTY = 1;
    public static final byte CACHED_MOREFULL = 2;

    boolean canEntityMerge();

    byte getCachedState();

    void setCachedState(byte state);

    boolean canMergeItself();

    boolean isMoreEmpty();
}
