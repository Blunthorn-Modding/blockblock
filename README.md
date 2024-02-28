# Welcome to the BlockBlock wiki!
BlockBlock is a mod that allows for restricting of things. It's meant to allow modpack creators to progressively unlock things for the player.

## Lock Categories
Here you'll find what each each category will do when an object is locked within them.
* **Breaking:** Can contain blocks. Blocks will not drop their item when broken.
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
`/bb reload` reloads the **_config only_**. It will not reload the default values file.<br>
<br>
`[LOCK_TYPE]` can be one of these values: `[block_interaction | breaking | crafting_recipe | entity_drop | entity_interaction | item_usage]` <br>
`[TARGET]` is the player(s) you want to lock/unlock something for. Supports @x targets or a specific player.<br>
`[TAG/OBJECT_ID]` can be either an object id (i.e. `minecraft:grass_block`) or a tag (i.e. `#minecraft:wool`). Other modded ID's or tags are supported<br> <br>**IMPORTANT:** while you can lock any modded object, if mods use their own systems to do things like handling interaction it will not actually lock it properly. This mod relies on the vanilla implementations. However, there are addon mods planned. If you REALLY want an addon for a mod, please [open an issue](https://github.com/WouterB15/blockblock/issues/new) stating which mod, and what part does not work as expected.

## Configs
### blockblock/blockblock_config.properties
**MESSAGES**<br>
This section contains the message that gets displayed for each locking category. If you want to display the _translated_ object name, use `{OBJECT}` as a placeholder.<br>
<br>
**GENERAL**<br>
`displayMessageToUser` - [true/false] - Whether messages are displayed to the user when an object is locked.<br>
<br>
**Default config:**<br>
```properties
# BlockBlock Config

# MESSAGES
# '{OBJECT}' will get replaced with the translated object name.
messageBreaking=You do not have {OBJECT} unlocked!
messageBlockInteraction=You do not have {OBJECT} unlocked!
messageEntityInteraction=You do not have {OBJECT} unlocked!
messageEntityDrop=You do not have {OBJECT} unlocked!
messageItemUsage=You do not have {OBJECT} unlocked!
messageRecipeUsage=You do not have {OBJECT} unlocked!

# GENERAL
# Whether the user will get the messages listed above or not
displayMessagesToUser=true

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
