package com.example.musketmod;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Draws the "Ammo [1/1]" readout just above the hotbar whenever the player is
 * holding a musket, plus a reload progress line while it is being loaded.
 */
public class AmmoHudLayer implements LayeredDraw.Layer {

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (!stack.is(MusketMod.MUSKET.get())) {
            stack = player.getOffhandItem();
            if (!stack.is(MusketMod.MUSKET.get())) {
                return;
            }
        }

        boolean loaded = MusketItem.isLoaded(stack);
        boolean reloading = player.isUsingItem() && player.getUseItem() == stack && !loaded;

        int centerX = graphics.guiWidth() / 2;
        int baseY = graphics.guiHeight() - 59;

        Component ammo = Component.literal("Ammo [" + (loaded ? 1 : 0) + "/1]")
                .withStyle(loaded ? ChatFormatting.WHITE : ChatFormatting.RED);
        graphics.drawString(mc.font, ammo, centerX - mc.font.width(ammo) / 2, baseY - 12, 0xFFFFFF, true);

        Component pouch = Component.literal("Balls: " + MusketItem.countAmmo(player))
                .withStyle(ChatFormatting.GRAY);
        graphics.drawString(mc.font, pouch, centerX - mc.font.width(pouch) / 2, baseY - 2, 0xFFFFFF, true);

        if (reloading) {
            int used = player.getTicksUsingItem();
            int percent = Math.min(100, used * 100 / MusketItem.RELOAD_TICKS);
            Component text = Component.literal("Reloading " + percent + "%")
                    .withStyle(ChatFormatting.YELLOW);
            graphics.drawString(mc.font, text, centerX - mc.font.width(text) / 2, baseY - 24, 0xFFFFFF, true);

            // simple progress bar under the text
            int barWidth = 80;
            int x = centerX - barWidth / 2;
            int y = baseY - 30;
            graphics.fill(x - 1, y - 1, x + barWidth + 1, y + 4, 0xAA000000);
            graphics.fill(x, y, x + (barWidth * percent / 100), y + 3, 0xFFE8BE3C);
        }
    }
}
