package com.github.tatercertified.potatoptimize.mixin.feature.interaction_limiter;

import com.github.tatercertified.potatoptimize.utils.interfaces.InteractionLimiterInterface;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class LimiterServerInteractionManagerMixin {
    @Shadow @Final protected ServerPlayerEntity player;

    @Shadow protected ServerWorld world;

    @Shadow protected abstract void onBlockBreakingAction(BlockPos pos, boolean success, int sequence, String reason);

    @Inject(method = "finishMining", at = @At("HEAD"), cancellable = true)
    private void countBlockBreaking(BlockPos pos, int sequence, String reason, CallbackInfo ci) {
        if (reason.equals("insta mine")) {
            ((InteractionLimiterInterface)player).addInstaBreakCountPerTick();
            if (!((InteractionLimiterInterface)player).allowOperation()) {
                this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, this.world.getBlockState(pos)));
                this.onBlockBreakingAction(pos, false, sequence, reason);
                ci.cancel();
            }
        }
    }
}
