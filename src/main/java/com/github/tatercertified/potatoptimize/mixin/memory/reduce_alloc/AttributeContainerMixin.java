package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

// Credit Gale patch #0040
@Mixin(AttributeContainer.class)
public abstract class AttributeContainerMixin {
    @Shadow @Final private DefaultAttributeContainer fallback;

    @Shadow protected abstract void updateTrackedStatus(EntityAttributeInstance instance);

    @Shadow @Final private Map<EntityAttribute, EntityAttributeInstance> custom;
    @Unique
    @Final
    @Mutable
    private java.util.function.Function<EntityAttribute, EntityAttributeInstance> createInstance;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectAttribute(DefaultAttributeContainer defaultAttributes, CallbackInfo ci) {
        this.createInstance = attribute -> this.fallback.createOverride(this::updateTrackedStatus, attribute);
    }

    /**
     * @author QPCrummer
     * @reason Reduce allocations
     */
    @Overwrite
    public @Nullable EntityAttributeInstance getCustomInstance(EntityAttribute attribute) {
        return this.custom.computeIfAbsent(attribute, this.createInstance);
    }
}
