# PacketMines

A Spigot plugin that creates virtual prison mines using PacketEvents API. Players are assigned to regions and can mine fake blocks that respawn instantly.

## Features

- **Virtual Block System**: Uses PacketEvents to send fake blocks to players without modifying the actual world
- **Region Management**: Create multiple mine regions to distribute players
- **Auto-Assignment**: Players are automatically assigned to a region on login
- **Dynamic Load Balancing**: Maximum 10 players per region (can exceed if needed)
- **Custom Pickaxe**: Special pickaxe with NBT tag for mining virtual blocks
- **Mine Customization**: Fill mines with any block material
- **Mine Expansion**: Expand player mines dynamically
- **Data Persistence**: Automatic saving/loading of regions and mines

## Commands

All commands require the `packetmines.admin` permission.

### `/amine wand`
- **Aliases**: `getwand`
- **Description**: Gives you the mine creation wand
- **Usage**: `/amine wand`
- **Permission**: `packetmines.admin.wand`

### `/amine create <regionId> [maxPlayers]`
- **Aliases**: `createregion`
- **Description**: Creates a new mine region using wand selection
- **Usage**: `/amine create region1 10`
- **Permission**: `packetmines.admin.create`
- **Notes**:
  - Use the wand to select two corners first
  - Default maxPlayers is 10 if not specified

### `/amine delete <regionId>`
- **Aliases**: `deleteregion`
- **Description**: Deletes an existing mine region
- **Usage**: `/amine delete region1`
- **Permission**: `packetmines.admin.delete`

### `/amine list`
- **Aliases**: `listregions`
- **Description**: Lists all mine regions and their status
- **Usage**: `/amine list`
- **Permission**: `packetmines.admin.list`

### `/amine setmineblock <player> <block>`
- **Aliases**: `setblock`, `fillmine`
- **Description**: Fills a player's mine with the specified block
- **Usage**: `/amine setmineblock Notch DIAMOND_ORE`
- **Permission**: `packetmines.admin.setmineblock`

### `/amine expand <player> <amount>`
- **Aliases**: `expandmine`
- **Description**: Expands a player's mine by the specified amount
- **Usage**: `/amine expand Notch 20`
- **Permission**: `packetmines.admin.expand`

### `/amine givepickaxe [player]`
- **Aliases**: `pickaxe`
- **Description**: Gives the custom mine pickaxe
- **Usage**: `/amine givepickaxe Notch`
- **Permission**: `packetmines.admin.givepickaxe`

## How It Works

### Mine Creation Flow

1. **Get the Wand**: Use `/amine wand` to get the selection tool
2. **Select Region**:
   - Left-click a block to set position 1
   - Right-click a block to set position 2
3. **Create Region**: Run `/amine create <regionId>` to create the region
4. **Player Assignment**: When players login, they are auto-assigned to a region
5. **Mine Creation**: Each player gets a default 10x10x10 mine within their region

### Virtual Blocks

- Blocks are sent using PacketEvents' multi-block change packets
- Blocks don't exist in the world - they're client-side only
- Breaking blocks with the custom pickaxe sends fake break animations
- Blocks "respawn" instantly (0.5 second delay)

### Region Assignment

- Players are assigned to the region with the fewest players
- If all regions are full (10+ players), assignment continues to the least-full region
- This ensures all players have a mine even during high load

## Configuration

### config.yml

```yaml
WAND:
  MATERIAL: 'GOLDEN_AXE'
  NAME: '&eMine Creation Wand'
  GLOW: true
  LORE:
    - '&7This wand allows you to create a region for a specific mine'
    - '&7by selecting a region, then creating it with &d/amine create <mine>'

PICKAXE:
  MATERIAL: 'DIAMOND_PICKAXE'
  NAME: '&bPacket Mine Pickaxe'
  GLOW: true
  LORE:
    - '&7This pickaxe allows you to mine in your packet mine'
    - '&7Blocks are virtual and will respawn instantly'
```

## Data Storage

- **regions.yml**: Stores all mine regions
- **mines.yml**: Stores all player mines
- Auto-saves every 5 minutes
- Saves on server shutdown

## Dependencies

- **Spigot/Paper**: 1.20.1+
- **PacketEvents**: 2.2.1 (bundled)

## Technical Details

### Core Components

- **MineManager**: Manages all mines and regions
- **PacketHandler**: Handles fake block packet sending via PacketEvents
- **FileManager**: Handles data persistence
- **DataListeners**: Handles player events and wand interactions

### Block State ID

The plugin uses a simplified block state ID system based on Material ordinals. For production use, consider implementing proper Minecraft block state ID mapping.

## Installation

1. Download the latest release
2. Place in your server's `plugins/` folder
3. Restart your server
4. Configure `config.yml` as needed
5. Create mine regions with the wand

## Permissions

- `packetmines.admin` - Access to all admin commands
- `packetmines.admin.wand` - Get the selection wand
- `packetmines.admin.create` - Create mine regions
- `packetmines.admin.delete` - Delete mine regions
- `packetmines.admin.list` - List mine regions
- `packetmines.admin.setmineblock` - Set mine block types
- `packetmines.admin.expand` - Expand player mines
- `packetmines.admin.givepickaxe` - Give custom pickaxe

## Support

For issues and feature requests, please create an issue on GitHub.

## License

This project is licensed under the MIT License.
