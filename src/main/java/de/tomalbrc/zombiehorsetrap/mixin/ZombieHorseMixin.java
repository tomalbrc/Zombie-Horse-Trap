package de.tomalbrc.zombiehorsetrap.mixin;

import de.tomalbrc.zombiehorsetrap.impl.IZombieHorseTrap;
import de.tomalbrc.zombiehorsetrap.impl.ZombieTrapGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ZombieHorse.class)
public abstract class ZombieHorseMixin extends AbstractHorse implements IZombieHorseTrap {
    @Unique private final ZombieTrapGoal zht$trapGoal = new ZombieTrapGoal(ZombieHorse.class.cast(this));
    @Unique private boolean zht$isTrap = false;
    @Unique private int zht$time = 0;

    protected ZombieHorseMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void zht$setTrap(boolean trap) {
        if (trap != this.zht$isTrap) {
            zht$isTrap = trap;
            if (trap) {
                this.goalSelector.addGoal(1, this.zht$trapGoal);
            } else {
                this.goalSelector.removeGoal(this.zht$trapGoal);
            }
        }
    }


    @Override
    public boolean zht$isTrap() {
        return zht$isTrap;
    }

    @Override
    public int zht$trapTime() {
        return zht$time;
    }

    @Override
    public void zht$setTrapTime(int time) {
        zht$time = time;
    }

    @Override
    public int zht$getAndIncreaseTrapTime() {
        return zht$time++;
    }
}
