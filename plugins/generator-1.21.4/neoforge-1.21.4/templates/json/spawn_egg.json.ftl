{
  "model": {
      "type": "minecraft:model",
      "model": "minecraft:item/template_spawn_egg",
      "tints": [
          {
              "type": "minecraft:constant",
              "value": ${data.spawnEggBaseColor.getRGB()}
          },
          {
              "type": "minecraft:constant",
              "value": ${data.spawnEggDotColor.getRGB()}
          }
      ]
  }
}