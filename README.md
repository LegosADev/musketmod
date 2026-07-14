# Musket Mod (NeoForge 1.21.1)

Adds a **Rifled Musket** and **Musket Balls** to Minecraft 1.21.1.

## Features
- **Rifled Musket** — deals **7 hearts (14 damage)** per shot
- **5 second reload** — hold right-click for 5 seconds to reload and fire
- **Musket Ball** — the musket's ammo, fired as a fast, flat-trajectory projectile
- Musket has 256 durability and appears in the Combat creative tab
- **One musket per hotbar + offhand** — any extra muskets are automatically moved to your main inventory (or dropped if it is full)

## Crafting
**Rifled Musket** (shaped, 3x3 grid):
- Diamond Block in the center
- Gold Block to the left of the center
- Stick in the bottom-right

**Musket Ball** (shapeless):
- 1 Gunpowder + 1 Iron Ingot -> 1 Musket Ball

## Building
Requires Java 21.

```
./gradlew build        # (or gradlew.bat build on Windows)
```

The finished mod jar will be in `build/libs/musketmod-1.0.0.jar`.
Drop it into your NeoForge 1.21.1 `mods` folder.

To test in a dev environment: `./gradlew runClient`

If you don't have the Gradle wrapper, install Gradle 8.8+ and run `gradle wrapper` once in this folder first.
