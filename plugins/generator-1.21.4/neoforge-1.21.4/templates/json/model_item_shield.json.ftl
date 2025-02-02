{
  "model": {
    "type": "minecraft:condition",
    "on_false": {
      "type": "minecraft:model",
      "model": "${modid}:item/${registryname}"
    },
    "on_true": {
      "type": "minecraft:model",
      "model": "${modid}:item/${registryname}_blocking"
    },
    "property": "minecraft:using_item"
  }
}