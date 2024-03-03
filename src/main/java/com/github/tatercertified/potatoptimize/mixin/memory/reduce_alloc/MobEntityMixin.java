package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import com.github.tatercertified.potatoptimize.utils.Constants;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Redirect(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EquipmentSlot;values()[Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot[] redirectValues() {
        return Constants.equipmentSlotArray;
    }

    @Redirect(method = "initEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EquipmentSlot;values()[Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot[] redirectValues1() {
        return Constants.equipmentSlotArray;
    }

    @Redirect(method = "updateEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EquipmentSlot;values()[Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot[] redirectValues2() {
        return Constants.equipmentSlotArray;
    }

    @Redirect(method = "convertTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EquipmentSlot;values()[Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot[] redirectValues3() {
        return Constants.equipmentSlotArray;
    }
}
