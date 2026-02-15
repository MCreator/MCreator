{
  "sources": [
    {
      "type": "paletted_permutations",
      "textures": [
        <#list armortrims as trim>
          "${modid}:trims/entity/humanoid/${trim.getModElement().getRegistryName()}",
          "${modid}:trims/entity/humanoid_leggings/${trim.getModElement().getRegistryName()}"<#sep>,
		</#list>
      ],
      "palette_key": "trims/color_palettes/trim_palette",
      "permutations": {
        "amethyst": "minecraft:trims/color_palettes/amethyst",
        "copper": "minecraft:trims/color_palettes/copper",
        "diamond": "minecraft:trims/color_palettes/diamond",
        "diamond_darker": "minecraft:trims/color_palettes/diamond_darker",
        "emerald": "minecraft:trims/color_palettes/emerald",
        "gold": "minecraft:trims/color_palettes/gold",
        "gold_darker": "minecraft:trims/color_palettes/gold_darker",
        "iron": "minecraft:trims/color_palettes/iron",
        "iron_darker": "minecraft:trims/color_palettes/iron_darker",
        "lapis": "minecraft:trims/color_palettes/lapis",
        "netherite": "minecraft:trims/color_palettes/netherite",
        "netherite_darker": "minecraft:trims/color_palettes/netherite_darker",
        "quartz": "minecraft:trims/color_palettes/quartz",
        "redstone": "minecraft:trims/color_palettes/redstone",
        "resin": "minecraft:trims/color_palettes/resin"
      }
    }
  ]
}