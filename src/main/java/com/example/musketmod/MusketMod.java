package com.example.musketmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod(MusketMod.MODID)
public class MusketMod {
    public static final String MODID = "musketmod";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public static final DeferredItem<Item> MUSKET_BALL = ITEMS.register("musket_ball",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> MUSKET = ITEMS.register("musket",
            () -> new MusketItem(new Item.Properties().durability(256).stacksTo(1)));

    public static final Supplier<EntityType<MusketBallEntity>> MUSKET_BALL_ENTITY =
            ENTITY_TYPES.register("musket_ball",
                    () -> EntityType.Builder.<MusketBallEntity>of(MusketBallEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("musket_ball"));

    public MusketMod(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(MUSKET);
            event.accept(MUSKET_BALL);
        }
    }
}
