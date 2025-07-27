package de.tomalbrc.zombiehorsetrap.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerLevel.class)
public interface ServerLevelInvoker {
    @Invoker
    BlockPos invokeFindLightningTargetAround(BlockPos blockPos);
}
