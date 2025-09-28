package de.tomalbrc.zombiehorsetrap.impl;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ZombieTrapGoal extends Goal {
    private final ZombieHorse horse;

    public ZombieTrapGoal(ZombieHorse skeletonHorse) {
        this.horse = skeletonHorse;
    }

    public boolean canUse() {
        return this.horse.level().hasNearbyAlivePlayer(this.horse.getX(), this.horse.getY(), this.horse.getZ(), (double)10.0F);
    }

    public void tick() {
        ServerLevel serverLevel = (ServerLevel)this.horse.level();
        DifficultyInstance difficultyInstance = serverLevel.getCurrentDifficultyAt(this.horse.blockPosition());
        ((IZombieHorseTrap)this.horse).zht$setTrap(false);
        this.horse.setTamed(true);
        this.horse.setAge(0);
        LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel, EntitySpawnReason.TRIGGERED);
        if (lightningBolt != null) {
            lightningBolt.setPos(this.horse.getX(), this.horse.getY(), this.horse.getZ());
            lightningBolt.setVisualOnly(true);
            serverLevel.addFreshEntity(lightningBolt);

            var witch = this.createWitch(difficultyInstance, this.horse.position(), this.horse.level());
            if (witch != null) {
                witch.startRiding(this.horse);
                serverLevel.addFreshEntityWithPassengers(witch);

                for (int i = 0; i < 3; ++i) {
                    AbstractHorse abstractHorse = this.createHorse(difficultyInstance);
                    if (abstractHorse != null) {
                        var zombie2 = this.createZombie(difficultyInstance, abstractHorse.position(), this.horse.level());
                        if (zombie2 != null) {
                            zombie2.startRiding(abstractHorse);
                            abstractHorse.push(this.horse.getRandom().triangle(0.0, 1.1485), 0.0, this.horse.getRandom().triangle(0.0, 1.1485));
                            serverLevel.addFreshEntityWithPassengers(abstractHorse);
                        }
                    }
                }

                for (int i = 0; i < 5; ++i) {
                    var zombie2 = this.createZombie(difficultyInstance, this.horse.position(), this.horse.level());
                    if (zombie2 != null) {
                        zombie2.push(this.horse.getRandom().triangle(0.0, 1.1485), 0.0, this.horse.getRandom().triangle(0.0, 1.1485));
                        serverLevel.addFreshEntity(zombie2);
                    }
                }
            }
        }
    }

    @Nullable
    private AbstractHorse createHorse(DifficultyInstance difficultyInstance) {
        ZombieHorse zombieHorse = EntityType.ZOMBIE_HORSE.create(this.horse.level(), EntitySpawnReason.TRIGGERED);
        if (zombieHorse != null) {
            zombieHorse.finalizeSpawn((ServerLevel)this.horse.level(), difficultyInstance, EntitySpawnReason.TRIGGERED, null);
            zombieHorse.setPos(this.horse.getX(), this.horse.getY(), this.horse.getZ());
            zombieHorse.invulnerableTime = 60;
            zombieHorse.setPersistenceRequired();
            zombieHorse.setTamed(true);
            zombieHorse.setAge(0);
        }

        return zombieHorse;
    }

    @Nullable
    private Zombie createZombie(DifficultyInstance difficultyInstance, Vec3 pos, Level level) {
        Zombie zombie = EntityType.ZOMBIE.create(level, EntitySpawnReason.TRIGGERED);
        if (zombie != null) {
            zombie.setBaby(level.getRandom().nextBoolean());
            zombie.finalizeSpawn((ServerLevel)level, difficultyInstance, EntitySpawnReason.TRIGGERED, null);
            zombie.setPos(pos);
            zombie.invulnerableTime = 60;
            zombie.setPersistenceRequired();
            if (zombie.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            }

            this.enchant(zombie, EquipmentSlot.MAINHAND, difficultyInstance);
            this.enchant(zombie, EquipmentSlot.HEAD, difficultyInstance);
        }

        return zombie;
    }

    @Nullable
    private Witch createWitch(DifficultyInstance difficultyInstance, Vec3 pos, Level level) {
        Witch witch = EntityType.WITCH.create(level, EntitySpawnReason.TRIGGERED);
        if (witch != null) {
            witch.finalizeSpawn((ServerLevel)level, difficultyInstance, EntitySpawnReason.TRIGGERED, null);
            witch.setPos(pos);
            witch.invulnerableTime = 60;
            witch.setPersistenceRequired();
        }

        return witch;
    }

    private void enchant(Zombie zombie, EquipmentSlot equipmentSlot, DifficultyInstance difficultyInstance) {
        ItemStack itemStack = zombie.getItemBySlot(equipmentSlot);
        itemStack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        EnchantmentHelper.enchantItemFromProvider(itemStack, zombie.level().registryAccess(), VanillaEnchantmentProviders.MOB_SPAWN_EQUIPMENT, difficultyInstance, zombie.getRandom());
        zombie.setItemSlot(equipmentSlot, itemStack);
    }
}
