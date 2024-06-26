package com.github.tatercertified.potatoptimize.mixin.feature.interaction_limiter;

import com.github.tatercertified.potatoptimize.utils.interfaces.InteractionLimiterInterface;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class LimiterBlockItemMixin {
    @Shadow protected abstract @Nullable BlockState getPlacementState(ItemPlacementContext context);

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;getPlacementState(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/block/BlockState;", shift = At.Shift.AFTER), cancellable = true)
    private void injectLimiter(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
        if (player != null && this.getPlacementState(context) != null) {
            ((InteractionLimiterInterface)player).addPlaceBlockCountPerTick();
            if (!((InteractionLimiterInterface)player).allowOperation()) {
                cir.setReturnValue(ActionResult.FAIL);
            }
        }
    }
}
