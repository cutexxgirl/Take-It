# TakeIt

A Minecraft Forge mod that completely changes how you pick up items!

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://www.minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.4.0-orange.svg)](https://files.minecraftforge.net/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 🎯 Features

- **🚫 No Automatic Pickup** - Items won't jump into your inventory just by walking over them
- **🖱️ Click to Pickup** - Right-click on items to pick them up individually
- **⌨️ Radius Pickup** - Press `G` (configurable) to pick up all items in a configurable radius
- **⚙️ Fully Configurable** - Customize pickup radius for X, Y, and Z axes independently
- **🌍 Multiplayer Compatible** - Works perfectly on both singleplayer and multiplayer servers

## 📦 Installation

1. Download and install [Minecraft Forge 1.20.1](https://files.minecraftforge.net/)
2. Download the latest release of TakeIt from [Modrinth](https://modrinth.com/mod/takeit) or [Releases](https://github.com/cutexxgirl/TakeIt/releases)
3. Place the `.jar` file in your `mods` folder
4. Launch Minecraft with the Forge 1.20.1 profile

## 🎮 How to Use

### Click to Pickup
Simply **right-click** on any item on the ground to pick it up!

### Radius Pickup
Press the **G key** (or your configured key) to pick up all items within your configured radius.

### Configuration
The mod creates a config file at `config/takeit-common.toml`:

```toml
[TakeIt Config]
    # Enable or disable the mod
    enableMod = true
    # Pickup radius in X direction (blocks)
    radiusX = 2
    # Pickup radius in Y direction (blocks)
    radiusY = 2
    # Pickup radius in Z direction (blocks)
    radiusZ = 2
```

You can also change the pickup key in **Options → Controls → TakeIt**

## 🔧 For Modpack Developers

TakeIt works great in modpacks with hundreds of mods! It's lightweight and doesn't conflict with other inventory/pickup mods.

**Recommended configs for large modpacks:**
- Keep radius small (2-3 blocks) to avoid lag
- Consider disabling in high-performance scenarios

## 💻 For Developers

Want to contribute or understand how it works?

### Project Structure
```
src/main/java/com/cutexxgirl/takeit/
├── TakeIt.java              # Main mod class
├── TakeItConfig.java        # Configuration handler
├── events/
│   ├── ClientEvents.java    # Client-side events (key presses, raycasting)
│   └── CommonEvents.java    # Common events (disabling auto-pickup)
└── network/
    ├── PacketHandler.java   # Network registration
    ├── PacketPickup.java    # Radius pickup packet
    └── PacketClickPickup.java # Click pickup packet
```

### Building from Source

```bash
git clone https://github.com/cutexxgirl/TakeIt.git
cd TakeIt
./gradlew build
```

The compiled `.jar` will be in `build/libs/`

### Key Implementation Details

- **Custom Raycast**: Vanilla Minecraft hitResult ignores ItemEntity, so we implement custom raycasting using `ProjectileUtil`
- **ThreadLocal Flag**: Uses ThreadLocal to distinguish between manual and automatic pickup
- **Network Packets**: Client-server communication for both click and radius pickup

## 🤝 Contributing

Contributions are welcome! Feel free to:
- Report bugs via [Issues](https://github.com/cutexxgirl/TakeIt/issues)
- Submit feature requests
- Create pull requests

## 📝 License

This mod is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**cutexxgirl**
- GitHub: [@cutexxgirl](https://github.com/cutexxgirl)
- Modrinth: [TakeIt](https://modrinth.com/mod/takeit)

## 🙏 Acknowledgments

- Thanks to the Forge team for the excellent modding API
- Thanks to the Minecraft modding community for resources and support

---

**Enjoy your new item pickup experience! 🎉**
