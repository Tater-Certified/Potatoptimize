package com.github.tatercertified.potatoptimize.mixin.memory.reduce_alloc;

import com.github.tatercertified.potatoptimize.utils.Constants;
import net.minecraft.scoreboard.AbstractTeam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(AbstractTeam.VisibilityRule.class)
public class AbstractTeamMixin {
    @Shadow @Final private static Map<String, AbstractTeam.VisibilityRule> VISIBILITY_RULES;

    /**
     * @author QPCrummer
     * @reason Reduce Allocations
     */
    @Overwrite
    public static String[] getKeys() {
        return VISIBILITY_RULES.keySet().toArray(Constants.emptyStringArray);
    }
}
