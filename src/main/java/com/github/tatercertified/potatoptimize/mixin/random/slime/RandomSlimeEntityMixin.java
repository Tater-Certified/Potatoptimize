package com.github.tatercertified.potatoptimize.mixin.random.slime;

import com.github.tatercertified.potatoptimize.utils.interfaces.SlimeChunkInterface;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SlimeEntity.class)
public abstract class RandomSlimeEntityMixin extends MobEntity implements Monster {
    protected RandomSlimeEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * @author QPCrummer
     * @reason This is just plain awful
     */
    @Overwrite
    public static boolean canSpawn(EntityType<SlimeEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        if (!(world instanceof StructureWorldAccess)) {
            return false;
        }

        if (SpawnReason.isAnySpawner(spawnReason)) {
            return canMobSpawn(type, world, spawnReason, pos, random);
        } else if (pos.getY() < 70 && pos.getY() > 50 ) {
            float randomFloat = random.nextFloat(); // This will slightly change parity
            if (randomFloat > 0.5f && randomFloat > world.getMoonSize() && world.getLightLevel(pos) <= random.nextInt(8)) {
                return canMobSpawn(type, world, spawnReason, pos, random);
            }
        }

        if (pos.getY() < 40 && random.nextInt(10) == 0) {
            WorldChunk chunk = (WorldChunk) world.getChunk(pos);
            if (((SlimeChunkInterface)chunk).isSlimeChunk()) {
                return canMobSpawn(type, world, spawnReason, pos, random);
            }
        }

        return false;
    }
}
