package com.github.tatercertified.potatoptimize.mixin.world.saving;

import com.github.tatercertified.potatoptimize.utils.interfaces.AsyncChunkManagerInterface;
import com.github.tatercertified.potatoptimize.utils.interfaces.AsyncChunkSaveInterface;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.*;

import java.io.File;
import java.util.Map;

@Mixin(PersistentStateManager.class)
public abstract class PersistentStateManagerMixin implements AsyncChunkManagerInterface {
    @Shadow @Final private Map<String, PersistentState> loadedStates;

    @Shadow protected abstract File getFile(String id);

    @Shadow @Final private RegistryWrapper.WrapperLookup registryLookup;

    @Unique
    @Override
    public void save(boolean async) {
        this.loadedStates.forEach((id, state) -> {
            if (state != null) {
                ((AsyncChunkSaveInterface)state).save(this.getFile(id), async, this.registryLookup);
            }

        });
    }

    /**
     * @author QPCrummer
     * @reason Redirect to my new method above
     * It stays false by default to not cause any saving issues in areas when upgrading the world.
     */
    @Overwrite
    public void save() {
        save(false);
    }
}
