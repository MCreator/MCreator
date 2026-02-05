<#-- @formatter:off -->
{
  "format_version": "1.21.40",
  "minecraft:block": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "menu_category": {
          "category": "<#if data.enableCreativeTab>${generator.map(data.creativeTab, "tabs")}<#else>none</#if>"
          <#if data.isHiddenInCommands>,"is_hidden_in_commands": true</#if>
      }
    },
    "components": {
      "minecraft:geometry": "minecraft:geometry.full_block",
      "minecraft:material_instances": {
        "up": {
          "texture": "${modid}_${registryname}_up"
        },
        "down": {
          "texture": "${modid}_${registryname}_down"
        },
        "north": {
          "texture": "${modid}_${registryname}_north"
        },
        "south": {
          "texture": "${modid}_${registryname}_south"
        },
        "east": {
          "texture": "${modid}_${registryname}_east"
        },
        "west": {
          "texture": "${modid}_${registryname}_west"
        }
      },
      <#if generator.map(data.colorOnMap, "mapcolors") != "DEFAULT">
      "minecraft:map_color": "${generator.map(data.colorOnMap, "mapcolors")}",
      </#if>
      <#if data.hasCustomDrop()>
      "minecraft:loot": "loot_tables/blocks/${modid}_${registryname}.json",
      </#if>
      "minecraft:light_emission": ${data.lightEmission},
      "minecraft:destructible_by_mining": {
        "seconds_to_destroy": ${data.hardness}
      },
      "minecraft:destructible_by_explosion": {
        "explosion_resistance": ${data.resistance}
      },
      "minecraft:friction": ${data.friction},
      "minecraft:flammable": {
        "catch_chance_modifier": ${data.flammability},
        "destroy_chance_modifier": ${data.flammableDestroyChance}
      }
    }
  }
}
<#-- @formatter:on -->