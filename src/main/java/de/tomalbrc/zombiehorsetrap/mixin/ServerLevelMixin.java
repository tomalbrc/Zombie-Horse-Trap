package de.tomalbrc.zombiehorsetrap.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.tomalbrc.zombiehorsetrap.ZombieHorseTrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerLevelMixin  {

    @ModifyExpressionValue(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I", ordinal = 0))
    private int zht$zombieHorseTrap(int original, @Local(argsOnly = true) LevelChunk chunk) {
        boolean spawn = original == 0;
        if (!spawn) {
            ZombieHorseTrap.spawnZombieHorseTrap((ServerLevel)(Object)this, chunk);
        }

        return original;
    }
}
