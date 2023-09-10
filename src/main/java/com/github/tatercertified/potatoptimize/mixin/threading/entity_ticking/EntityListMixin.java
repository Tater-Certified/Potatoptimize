package com.github.tatercertified.potatoptimize.mixin.threading.entity_ticking;

import net.minecraft.entity.Entity;
import net.minecraft.world.EntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Mixin(EntityList.class)
public class EntityListMixin {
    @Unique
    private final ConcurrentHashMap<Integer, Entity> concurrentEntities = new ConcurrentHashMap<>();

    /**
     * @author QPCrummer
     * @reason Make Concurrent
     */
    @Overwrite
    public void add(Entity entity) {
        this.concurrentEntities.put(entity.getId(), entity);
    }

    /**
     * @author QPCrummer
     * @reason Make Concurrent
     */
    @Overwrite
    public void remove(Entity entity) {
        concurrentEntities.remove(entity.getId());
    }

    /**
     * @author QPCrummer
     * @reason Make Concurrent
     */
    @Overwrite
    public boolean has(Entity entity) {
        return concurrentEntities.containsKey(entity.getId());
    }

    /**
     * @author QPCrummer
     * @reason Make Concurrent
     */
    @Overwrite
    public void forEach(Consumer<Entity> action) {
        this.concurrentEntities.forEach(6, (key, value) -> action.accept(value));
    }
}
