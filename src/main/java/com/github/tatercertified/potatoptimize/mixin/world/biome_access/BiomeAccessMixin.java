package com.github.tatercertified.potatoptimize.mixin.world.biome_access;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.SeedMixer;
import org.spongepowered.asm.mixin.*;

/**
 * Credit to FXMorin (Unmerged PR for Lithium)
 */
@Mixin(BiomeAccess.class)
public class BiomeAccessMixin {
    @Shadow
    @Final
    private BiomeAccess.Storage storage;

    @Shadow
    @Final
    private long seed;

    @Shadow
    private static double method_38108(long l) {return 0;}

    @Unique
    private static final double maxOffset = 0.4500000001D;

    /**
     * @author FX - PR0CESS
     * @reason I wanted to make it faster
     */
    @Overwrite
    public RegistryEntry<Biome> getBiome(BlockPos pos) {
        int xMinus2 = pos.getX() - 2;
        int yMinus2 = pos.getY() - 2;
        int zMinus2 = pos.getZ() - 2;
        int x = xMinus2 >> 2;
        int y = yMinus2 >> 2;
        int z = zMinus2 >> 2;
        double quartX = (double)(xMinus2 & 3) / 4.0D;
        double quartY = (double)(yMinus2 & 3) / 4.0D;
        double quartZ = (double)(zMinus2 & 3) / 4.0D;
        int smallestX = 0;
        double smallestDist = Double.POSITIVE_INFINITY;
        for(int biomeX = 0; biomeX < 8; ++biomeX) {
            boolean everyOtherQuad = (biomeX & 4) == 0;
            boolean everyOtherPair = (biomeX & 2) == 0;
            boolean everyOther =     (biomeX & 1) == 0;
            double quartXX = everyOtherQuad ? quartX : quartX - 1.0D;
            double quartYY = everyOtherPair ? quartY : quartY - 1.0D;
            double quartZZ = everyOther     ? quartZ : quartZ - 1.0D;

            double maxQuartYY = 0.0D,maxQuartZZ = 0.0D;
            if (biomeX != 0) {
                maxQuartYY = MathHelper.square(Math.max(quartYY + maxOffset, Math.abs(quartYY - maxOffset)));
                maxQuartZZ = MathHelper.square(Math.max(quartZZ + maxOffset, Math.abs(quartZZ - maxOffset)));
                double maxQuartXX = MathHelper.square(Math.max(quartXX + maxOffset, Math.abs(quartXX - maxOffset)));
                if (smallestDist < maxQuartXX + maxQuartYY + maxQuartZZ) {
                    continue;
                }
            }

            int xx = everyOtherQuad ? x : x + 1;
            int yy = everyOtherPair ? y : y + 1;
            int zz = everyOther ? z : z + 1;

            long seed = SeedMixer.mixSeed(this.seed, xx);
            seed = SeedMixer.mixSeed(seed, yy);
            seed = SeedMixer.mixSeed(seed, zz);
            seed = SeedMixer.mixSeed(seed, xx);
            seed = SeedMixer.mixSeed(seed, yy);
            seed = SeedMixer.mixSeed(seed, zz);
            double offsetX = method_38108(seed);
            double sqrX = MathHelper.square(quartXX + offsetX);
            if (biomeX != 0 && smallestDist < sqrX + maxQuartYY + maxQuartZZ) {
                continue;
            }
            seed = SeedMixer.mixSeed(seed, this.seed);
            double offsetY = method_38108(seed);
            double sqrY = MathHelper.square(quartYY + offsetY);
            if (biomeX != 0 && smallestDist < sqrX + sqrY + maxQuartZZ) {
                continue;
            }
            seed = SeedMixer.mixSeed(seed, this.seed);
            double offsetZ = method_38108(seed);
            double biomeDist = sqrX + sqrY + MathHelper.square(quartZZ + offsetZ);

            if (smallestDist > biomeDist) {
                smallestX = biomeX;
                smallestDist = biomeDist;
            }
        }

        int biomeX = (smallestX & 4) == 0 ? x : x + 1;
        int biomeY = (smallestX & 2) == 0 ? y : y + 1;
        int biomeZ = (smallestX & 1) == 0 ? z : z + 1;
        return this.storage.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }
}
