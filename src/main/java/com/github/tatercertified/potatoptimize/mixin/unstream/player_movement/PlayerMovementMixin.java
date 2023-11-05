package com.github.tatercertified.potatoptimize.mixin.unstream.player_movement;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ServerPlayNetworkHandler.class)
public class PlayerMovementMixin {
    /**
     * @author Maity (stale Lithium PR)
     * @reason Remove streams
     */
    @Overwrite
    private boolean isEntityOnAir(Entity entity) {
        Box box = entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0);

        int minX = MathHelper.floor(box.minX);
        int minY = MathHelper.floor(box.minY);
        int minZ = MathHelper.floor(box.minZ);
        int maxX = MathHelper.floor(box.maxX);
        int maxY = MathHelper.floor(box.maxY);
        int maxZ = MathHelper.floor(box.maxZ);

        World world = entity.getWorld();
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    pos.set(x, y, z);

                    if (!world.getBlockState(pos).isAir()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
