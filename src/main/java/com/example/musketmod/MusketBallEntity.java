package com.example.musketmod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class MusketBallEntity extends ThrowableItemProjectile {

    public MusketBallEntity(EntityType<? extends MusketBallEntity> type, Level level) {
        super(type, level);
    }

    public MusketBallEntity(Level level, LivingEntity shooter) {
        super(MusketMod.MUSKET_BALL_ENTITY.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return MusketMod.MUSKET_BALL.get();
    }

    @Override
    protected double getDefaultGravity() {
        // Flatter trajectory than a snowball, like a bullet
        return 0.01;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), MusketItem.DAMAGE);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            // Entity event 3 spawns the item break particles on the client
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }
}
