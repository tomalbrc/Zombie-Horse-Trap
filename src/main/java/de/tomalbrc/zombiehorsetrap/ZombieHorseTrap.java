package de.tomalbrc.zombiehorsetrap;

import de.tomalbrc.zombiehorsetrap.impl.IZombieHorseTrap;
import de.tomalbrc.zombiehorsetrap.mixin.ServerLevelInvoker;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public class ZombieHorseTrap implements ModInitializer {

    @Override
    public void onInitialize() {
    }

    public static void spawnZombieHorseTrap(ServerLevel serverLevel, LevelChunk levelChunk) {
        boolean raining = serverLevel.isRaining();
        if (raining && serverLevel.getRandom().nextInt(10_000) == 0) {
            ChunkPos chunkPos = levelChunk.getPos();
            int minBlockX = chunkPos.getMinBlockX();
            int minBlockZ = chunkPos.getMinBlockZ();

            BlockPos blockPos = ((ServerLevelInvoker)(serverLevel)).invokeFindLightningTargetAround(serverLevel.getBlockRandomPos(minBlockX, 0, minBlockZ, 15));
            if (serverLevel.isRainingAt(blockPos)) {
                DifficultyInstance difficulty = serverLevel.getCurrentDifficultyAt(blockPos);
                boolean canSpawn = serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && serverLevel.random.nextDouble() < (double)difficulty.getEffectiveDifficulty() * 0.01 && !serverLevel.getBlockState(blockPos.below()).is(Blocks.LIGHTNING_ROD);
                if (canSpawn) {
                    ZombieHorse horse = EntityType.ZOMBIE_HORSE.create(serverLevel);
                    if (horse != null) {
                        ((IZombieHorseTrap)horse).zht$setTrap(true);
                        horse.setAge(0);
                        horse.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        serverLevel.addFreshEntity(horse);
                    }
                }

                LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
                if (lightningBolt != null) {
                    lightningBolt.moveTo(Vec3.atBottomCenterOf(blockPos));
                    lightningBolt.setVisualOnly(canSpawn);
                    serverLevel.addFreshEntity(lightningBolt);
                }
            }
        }
    }
}
