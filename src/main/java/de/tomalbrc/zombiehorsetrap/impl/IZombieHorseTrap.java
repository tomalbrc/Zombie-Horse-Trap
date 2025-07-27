package de.tomalbrc.zombiehorsetrap.impl;

public interface IZombieHorseTrap {
    void zht$setTrap(boolean trap);
    boolean zht$isTrap();

    int zht$trapTime();
    void zht$setTrapTime(int time);
    int zht$getAndIncreaseTrapTime();
}
