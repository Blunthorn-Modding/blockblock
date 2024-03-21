# Welcome to the BlockBlock wiki!
BlockBlock is a mod that allows for restricting of things. It's meant to allow modpack creators to progressively unlock things for the player.

## Lock Categories
Here you'll find what each each category will do when an object is locked within them.
* **Breaking:** Can contain blocks. Blocks will not drop their item when broken and break slower, or become unbreakable depending on config setting.
* **Placement:** Can contain blocks. Blocks will not be placeable.
* **Block Interaction:** Can contain blocks. The player cannot interact with blocks using their [RMB]. This is not limited to full blocks like crafting tables or furnaces, but also works for non-full blocks like chests, campfires, doors and redstonecomponents.
* **Entity Interaction:** Can contain entities. Player cannot interact with entities, like feeding, milking or using an armor stand.
* **Entity Drop:** Can contain entities. Entities will not drop loot or experience.
* **Item Usage:** Can contain items. Items cannot be used by the player in any way by left- or rightclicking. This will lock:
    * Weapons
    * Tools
    * Armor
    * Shields
    * Food
    * Potions
    * Totems of Undying
* **Crafting Recipe:** Can contain items. Will prevent crafting of items. Works for the crafting bench, player inventory crafting grid and the smithing table.

## Commands
Operators can use the following commands:<br>
`/bb lock [LOCK_TYPE] [TARGET] [TAG/OBJECT_ID]` will lock a tag or object in the given lock category.<br>
`/bb unlock [LOCK_TYPE] [TARGET] [TAG/OBJECT_ID]` will unlock a tag or object in the given lock category.<br>
`/bb reset [TARGET] [WIPE]` will either reset the target player(s) to the default values, or remove all restrictions depending on the `wipe` argument. The `wipe` argument is optional. If left out, will just reset to default values. <br>
`/bb reload` reloads the config and the default values file.<br>
<br>
`[LOCK_TYPE]` can be one of these values: `[block_interaction | breaking | placement | crafting_recipe | entity_drop | entity_interaction | item_usage]` <br>
`[TARGET]` is the player(s) you want to lock/unlock something for. Supports @x targets or a specific player.<br>
`[TAG/OBJECT_ID]` can be either an object id (i.e. `minecraft:grass_block`) or a tag (i.e. `#minecraft:wool`). Other modded ID's or tags are supported<br> <br>**IMPORTANT:** while you can lock any modded object, if mods use their own systems to do things like handling interaction it will not actually lock it properly. This mod relies on the vanilla implementations. However, there are mod integrations planned. If you REALLY want an integration for a mod, please [open an issue](https://github.com/WouterB15/blockblock/issues/new) stating which mod, and what part does not work as expected.

## Configs
### blockblock/blockblock_config.properties
**MESSAGES**<br>
This section contains the message that gets displayed for each locking category. If you want to display the _translated_ object name, use whichever value is set in the `objectIdPlaceholder` field as a placeholder. By default this is `{OBJECT}`.<br>
<br>
**GENERAL**<br>
`displayMessageToUser` - [true/false] - Whether messages are displayed above the hotbar to the user when an object is locked.<br>
`creativeBypassesRestrictions` - [true/false] - Whether players in creative mode are affected by locked objects or not.<br>
`breakingLockedPreventsBreaking` - [true/false] - Whether blocks that are marked as locked in the `breaking` category are unbreakable for that player. This means that any attempt to mine a block using a hand or tool will never succeed. TNT can still be used, but will not yield drops.<br>
`lockedBreakTimeModifier` - [float] - This value controls how much slower it will be to mine locked blocks in the breaking category. Higher values are slower. Setting this to `0` will result in instant mining. Always keep this value above `0`.
<br>
**Default config:**<br>
```properties
# BlockBlock Config

# MESSAGES
# The value of 'objectIdPlaceholder' will get replaced with the translated object name.
objectIdPlaceholder={OBJECT}
messageBreaking=You do not have {OBJECT} unlocked!
messagePlacement=You do not have {OBJECT} unlocked!
messageBlockInteraction=You do not have {OBJECT} unlocked!
messageEntityInteraction=You do not have {OBJECT} unlocked!
messageEntityDrop=You do not have {OBJECT} unlocked!
messageItemUsage=You do not have {OBJECT} unlocked!
messageRecipeUsage=You do not have {OBJECT} unlocked!

# GENERAL
# Whether the user will get the messages listed above or not
displayMessagesToUser=true
# If true, players in creative will not be affected by locked objects
creativeBypassesRestrictions=true
# If true, a locked block in category 'breaking' will become unbreakable by mining with hand/tool
breakingLockedPreventsBreaking=false
# This value determines how much longer it takes when trying to break a block that is locked.
# Higher is slower. Value should be at least 0. Calculation: deltaBreakTime / lockedBreakTimeModifier
lockedBreakTimeModifier=5.0

```

### blockblock/blockblock_default_values.json
Contains the values that get assigned to a player when first joining the world. Here you'll find the categories that you can lock things in. Simply add the object or tag you want to lock to the category you want to lock it behind. By default it will generate with empty lists, however, below you'll find a small example.<br>
Keep in mind, tags need to be preceded with a `#`!
```json
{
  "breaking": [
    "minecraft:dirt",
    "#minecraft:wool"
  ],
  "placement": [
    "minecraft:tnt"
  ],
  "block_interaction": [
    "minecraft:furnace"
  ],
  "entity_interaction": [
    "minecraft:cow"
  ],
  "entity_drop": [
    "minecraft:zombie"
  ],
  "item_usage": [
    "minecraft:diamond_sword",
    "minecraft:fishing_rod",
    "minecraft:diamond_pickaxe"
  ],
  "crafting_recipe": [
    "minecraft:netherite_scrap",
    "minecraft:netherite_sword"
  ]
}
```
