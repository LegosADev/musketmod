# Musket Mod (NeoForge 1.21.1)

Adds a **Rifled Musket** and **Musket Balls** to Minecraft 1.21.1.

## Features
- **Rifled Musket** — deals **7 hearts (14 damage)** per shot
- **Crossbow-style loading** — hold right-click for 5 seconds to ram a ball home. The musket
  then *stays loaded* until you pull the trigger, even if you swap slots. Click to fire instantly.
- **Custom 3D model** — barrel, walnut stock, brass bands and trigger guard. The hammer visibly
  sits at half-cock while loading and springs back to full cock once loaded.
- **Ammo HUD** — an `Ammo [1/1]` readout above the hotbar, your remaining ball count, and a
  reload progress bar while loading.
- **Muzzle smoke and flash** on firing, with a flat, fast bullet trajectory
- Musket has 256 durability and appears in the Combat creative tab
- **One musket per hotbar + offhand** — any extra muskets are automatically moved to your main inventory (or dropped if it is full)

## Crafting
**Rifled Musket** (shaped):
- Diamond Block in the center
- Gold Block to the left of the center
- Stick in the bottom-right

**Musket Ball** (shapeless):
- 1 Gunpowder + 1 Iron Ingot -> 1 Musket Ball

## Building
Requires Java 21. Run `gradle build` (or `gradlew build`); the jar lands in `build/libs/`.

This repo also builds automatically on GitHub via `.github/workflows/build.yml` —
push, open the **Actions** tab, and download the `musketmod-jar` artifact.

Drop the jar into a NeoForge 1.21.1 `mods` folder.
