package com.example.musketmod;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * A muzzle-loading rifled musket that behaves like a crossbow rather than a bow:
 * you spend 5 seconds ramming a ball home, the musket then *holds* that charge
 * indefinitely, and the next right-click fires it instantly.
 */
public class MusketItem extends Item {
    /** 5 seconds * 20 ticks = 100 ticks to reload. */
    public static final int RELOAD_TICKS = 100;
    /** 7 hearts = 14 damage. */
    public static final float DAMAGE = 14.0F;
    /** Short cooldown after firing so it can't be spam-clicked. */
    public static final int FIRE_COOLDOWN_TICKS = 10;

    public MusketItem(Properties properties) {
        super(properties);
    }

    public static boolean isLoaded(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(MusketMod.LOADED.get()));
    }

    public static void setLoaded(ItemStack stack, boolean loaded) {
        stack.set(MusketMod.LOADED.get(), loaded);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Already charged: pull the trigger.
        if (isLoaded(stack)) {
            fire(level, player, stack, hand);
            return InteractionResultHolder.consume(stack);
        }

        // Empty: start the 5 second reload if we have a ball for it.
        if (player.getAbilities().instabuild || !findAmmo(player).isEmpty()) {
            player.startUsingItem(hand);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROSSBOW_LOADING_START, SoundSource.PLAYERS, 0.9F, 0.7F);
            return InteractionResultHolder.consume(stack);
        }

        // No ammo: dry click.
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS, 0.5F, 1.8F);
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return RELOAD_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    /** Called when the player has held right-click for the full 5 seconds. */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player) || isLoaded(stack)) {
            return stack;
        }

        boolean creative = player.getAbilities().instabuild;
        ItemStack ammo = findAmmo(player);
        if (!creative && ammo.isEmpty()) {
            return stack;
        }
        if (!creative) {
            ammo.shrink(1);
        }

        setLoaded(stack, true);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS, 1.0F, 1.0F);
        return stack;
    }

    private void fire(Level level, Player player, ItemStack stack, InteractionHand hand) {
        setLoaded(stack, false);

        if (!level.isClientSide) {
            MusketBallEntity ball = new MusketBallEntity(level, player);
            // High velocity, very little spread - it is a rifle, after all.
            ball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 5.0F, 0.25F);
            level.addFreshEntity(ball);

            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

            if (level instanceof ServerLevel serverLevel) {
                Vec3 look = player.getLookAngle();
                Vec3 muzzle = player.getEyePosition().add(look.scale(1.4)).add(0.0, -0.15, 0.0);
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                        muzzle.x, muzzle.y, muzzle.z, 10, 0.06, 0.06, 0.06, 0.02);
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        muzzle.x, muzzle.y, muzzle.z, 4, 0.02, 0.02, 0.02, 0.01);
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 2.0F, 0.6F);
        player.getCooldowns().addCooldown(this, FIRE_COOLDOWN_TICKS);
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    /** Finds the first stack of musket balls in the player's inventory. */
    private static ItemStack findAmmo(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(MusketMod.MUSKET_BALL.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    /** Counts every musket ball the player is carrying, for the HUD. */
    public static int countAmmo(Player player) {
        int total = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(MusketMod.MUSKET_BALL.get())) {
                total += stack.getCount();
            }
        }
        return total;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.musketmod.musket.tooltip").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(isLoaded(stack)
                        ? "item.musketmod.musket.state_loaded"
                        : "item.musketmod.musket.state_empty")
                .withStyle(isLoaded(stack) ? ChatFormatting.GREEN : ChatFormatting.RED));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
