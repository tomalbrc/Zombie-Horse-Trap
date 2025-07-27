package de.tomalbrc.zombiehorsetrap.mixin;

import de.tomalbrc.zombiehorsetrap.impl.IZombieHorseTrap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {
    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;level()Lnet/minecraft/world/level/Level;", ordinal = 0), cancellable = true)
    private void zht$trapTick(CallbackInfo ci) {
        if ((Object)this instanceof ZombieHorse zombieHorse && ((IZombieHorseTrap)zombieHorse).zht$isTrap()) {
            if (((IZombieHorseTrap)zombieHorse).zht$getAndIncreaseTrapTime() >= 18000) {
                this.discard();
            }
            ci.cancel();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "RETURN"))
    public void zht$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        if ((Object)this instanceof ZombieHorse) {
            compoundTag.putBoolean("ZombieTrap", ((IZombieHorseTrap)this).zht$isTrap());
            compoundTag.putInt("ZombieTrapTime", ((IZombieHorseTrap)this).zht$trapTime());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "RETURN"))
    public void zht$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        if ((Object)this instanceof ZombieHorse) {
            if (compoundTag.contains("ZombieTrap")) ((IZombieHorseTrap)this).zht$setTrap(compoundTag.getBoolean("ZombieTrap"));
            if (compoundTag.contains("ZombieTrapTime")) ((IZombieHorseTrap)this).zht$setTrapTime(compoundTag.getInt("ZombieTrapTime"));
        }
    }
}
