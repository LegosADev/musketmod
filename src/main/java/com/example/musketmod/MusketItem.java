package com.example.musketmod;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

import java.util.List;

public class MusketItem extends Item {
    /** 5 seconds * 20 ticks = 100 ticks reload time */
    public static final int RELOAD_TICKS = 100;
    /** 7 hearts = 14 damage */
    public static final float DAMAGE = 14.0F;

    public MusketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getAbilities().instabuild || !findAmmo(player).isEmpty()) {
            player.startUsingItem(hand);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CROSSBOW_LOADING_START, SoundSource.PLAYERS, 1.0F, 0.8F);
            return InteractionResultHolder.consume(stack);
        }
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

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return stack;
        }

        boolean creative = player.getAbilities().instabuild;
        ItemStack ammo = findAmmo(player);

        if (creative || !ammo.isEmpty()) {
            if (!creative) {
                ammo.shrink(1);
            }

            if (!level.isClientSide) {
                MusketBallEntity ball = new MusketBallEntity(level, player);
                ball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 4.0F, 0.5F);
                level.addFreshEntity(ball);

                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(
                        player.getUsedItemHand() == null ? InteractionHand.MAIN_HAND : player.getUsedItemHand()));
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 1.5F, 0.7F);

            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return stack;
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.musketmod.musket.tooltip").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
