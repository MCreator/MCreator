<#-- @formatter:off -->
{
  "format_version": "1.21.50",
  "minecraft:item": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "menu_category": {
      	category: "items"
      }
    },
    "components": {
      "minecraft:icon": "${registryname}",
      "minecraft:glint": ${data.hasGlint},
      "minecraft:max_stack_size": ${data.stackSize},
      <#if data.maxDurability gt 0>
      "minecraft:durability": {
        "max_durability": ${data.maxDurability}
      },
      </#if>
      <#if data.isFood>
      "minecraft:use_modifiers": {
      	"use_duration": ${data.useDuration}
      },
      "minecraft:food": {
        "nutrition": ${data.foodNutritionalValue},
        "saturation_modifier": ${data.foodSaturation},
        "can_always_eat": ${data.foodCanAlwaysEat}
      },
      "minecraft:use_animation": "eat",
      "minecraft:tags": {
      	"tags": [
      		"minecraft:is_food"
      	]
      }
      </#if>
    }
  }
}
<#-- @formatter:on -->