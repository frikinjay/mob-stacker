<center>
<img src="[https://i.ibb.co/hf7t4tz/req-al-mr-335x130.png](https://media.forgecdn.net/attachments/988/553/mobstacker-1.png)" alt="An entity stacked to almost maximum integer limit">
<a href="https://modrinth.com/mod/almanac-lib">
<img src="https://i.ibb.co/hf7t4tz/req-al-mr-335x130.png" alt="Requires Almanac">
</a>
<a href="https://modrinth.com/mod/lmd">
<img src="https://i.ibb.co/HVg2LR9/req-lmd-mr-335x130.png" alt="Requires Let Me Despawn">
</a>
<a href="https://discord.gg/aPPEPJWG39">
<img src="https://i.ibb.co/GFT3JFP/req-discord-130x130.png" alt="Discord Server Invite Link">
</a>
</center>
<br>

**MobStacker** is a performance Minecraft mod to optimize entity handling, addressing a common cause of performance issues in vanilla and modded environments. By intelligently "stacking" similar mobs in close proximity, MobStacker significantly reduces server load and enhances client-side performance without compromising gameplay mechanics.

> 💡 **Note**: MobStacker preserves all loot and mob properties within stacked entities. Named mobs (via name tags) are exempt from stacking to maintain uniqueness.

## Key Benefits

1. **🚀 Server Performance Boost**: Dramatically reduces entity processing overhead.
2. **📈 Enhanced Client FPS**: Lowers strain on client-side rendering.
3. **🌐 Optimized Network Traffic**: Minimizes entity data transmission.
4. **🎮 Cleaner Gameplay**: Reduces visual clutter in mob-dense areas.
5. **💾 Memory Usage Reduction**: Lowers overall memory footprint.

## Performance Improvement

While actual performance gains vary based on server specifications, player count, and mod configurations, MobStacker can provide substantial improvements, especially in environments with:

- High mob density
- Numerous entity-adding mods
- Large, active player bases

> 🔗 **Recommended Companion Mods**:
> - [Let Me Despawn](https://www.curseforge.com/minecraft/mc-mods/let-me-despawn) (Required for despawn handling)
> - [Spawncap Control Utility](https://www.curseforge.com/minecraft/mc-mods/spawncapcontrolutility) or [In Control!](https://www.curseforge.com/minecraft/mc-mods/in-control) (Enhanced mob control)

## Features

| Feature | Description |
|---------|-------------|
| 📦 Smart Stacking | Automatically combines identical mob types within a configurable radius |
| 🔢 Customizable Stacks | Set preferred maximum stack sizes |
| ❤️ Flexible Health Management | Configurable options for stack health and death mechanics |
| 🚫 Selective Stacking | Ability to exclude specific entities or entire mods |
| 🔪 Stack Splitting | Implemented separator item functionality for dividing stacks |

## Configuration

| Option | Description | Default |
|--------|-------------|---------|
| `killWholeStackOnDeath` | Determines if entire stack dies when one mob is killed | `false` |
| `stackHealth` | Combines health of stacked mobs when enabled | `false` |
| `maxMobStackSize` | Maximum number of mobs in a single stack | `16` |
| `stackRadius` | Radius within which mobs attempt to stack | `6.0` |
| `enableSeparator` | Toggles use of separator item for stack splitting | `false` |
| `consumeSeparator` | Determines if separator item is consumed on use | `true` |
| `separatorItem` | Specifies the item used as a separator | `"minecraft:diamond"` |
| `ignoredEntities` | List of entities excluded from stacking | `["minecraft:ender_dragon"]` |
| `ignoredMods` | List of mod IDs whose entities are excluded from stacking | `["corpse"]` |

## Commands

All commands require operator permissions (level 2) and are prefixed with `/mobstacker`.

### Configuration Commands

```bash
# Toggle whole stack death
/mobstacker stackerConfig killWholeStackOnDeath [true|false]

# Toggle health stacking
/mobstacker stackerConfig stackHealth [true|false]

# Set maximum stack size
/mobstacker stackerConfig maxStackSize [value]

# Set stack radius
/mobstacker stackerConfig stackRadius [value]

# Toggle separator functionality
/mobstacker stackerConfig separator enableSeparator [true|false]

# Toggle separator consumption
/mobstacker stackerConfig separator consumeSeparator [true|false]

# Set separator item
/mobstacker stackerConfig separator separatorItem [item_id]
```

### Entity and Mod Management

```bash
# Ignore specific entity
/mobstacker ignore entity [entity_id]

# Ignore all entities from a mod
/mobstacker ignore mod [mod_id]

# Remove entity from ignore list
/mobstacker unignore entity [entity_id]

# Remove mod from ignore list
/mobstacker unignore mod [mod_id]

# Set stack size for specific entity
/mobstacker setStackSize [entity] [size]
```
**NOTE**: An entity can be given the tag `{StackData: {CanStack:0b}}` to prevent it from stacking.
### Mob Cap Management

```bash
# Set mob cap for mob categories
/mobstacker mobCapConfig [options]
```

## Additional Notes

- 🔄 Automatic stacking occurs when compatible mobs move to a new block.
- 👑 Boss entities receive special handling to preserve custom names and health bars.
- 🔌 API available for custom merging conditions, death handlers, and entity data modifiers.
- 📊 Stacked mobs display stack size in their name (e.g., "Zombie x5").
- 🐑🐷🧟 Compatible with various entity types: animals, monsters, and NPCs.

---

*Report issues to the issue tracker on github.*
