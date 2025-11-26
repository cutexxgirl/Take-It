# TakeIt

Minecraft Forge mod that changes item pickup mechanics.

## Features

- Disables automatic pickup when walking over items
- Right-click to pickup items
- Press G to pickup items in radius
- Configurable pickup radius

## Installation

1. Install [Minecraft Forge 1.20.1](https://files.minecraftforge.net/)
2. Download TakeIt from [Modrinth](https://modrinth.com/mod/takeit) or [Releases](https://github.com/cutexxgirl/TakeIt/releases)
3. Put the `.jar` file in your `mods` folder
4. Launch the game

## Usage

**Right-click** on an item to pick it up.

Press **G** to pickup all items in radius (configurable in controls).

## Configuration

Config file: `config/takeit-common.toml`

```toml
enableMod = true    # Enable/disable mod
radiusX = 2         # Pickup radius X axis
radiusY = 2         # Pickup radius Y axis  
radiusZ = 2         # Pickup radius Z axis
```

Change keybind in **Options → Controls → TakeIt**

## Building

```bash
git clone https://github.com/cutexxgirl/TakeIt.git
cd TakeIt
./gradlew build
```

## License

MIT License - see [LICENSE](LICENSE)
