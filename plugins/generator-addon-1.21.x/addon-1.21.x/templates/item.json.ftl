<#-- @formatter:off -->
<#include "mcitems.ftl">
{
  "format_version": "1.21.50",
  "minecraft:item": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "menu_category": {
      	"category": "${generator.map(data.creativeTab, "tabs")}"
      	<#if data.isHiddenInCommands>,"is_hidden_in_commands": true</#if>
      }
    },
    "components": {
      "minecraft:icon": "${registryname}",
      <#if data.hasGlint>"minecraft:glint": true,</#if>
      <#if data.allowOffHand>"minecraft:allow_off_hand": true,</#if>
      <#if data.handEquipped>"minecraft:hand_equipped": true,</#if>
      <#if !data.shouldDespawn>"minecraft:should_despawn": false,</#if>
      <#if data.rarity != "common">"minecraft:rarity": "${data.rarity}",</#if>
      <#if data.fuelDuration gt 0.05>
      "minecraft:fuel": {
          "duration": ${data.fuelDuration}
      },
      </#if>
      <#if data.maxDurability gt 0>
      "minecraft:durability": {
        "max_durability": ${data.maxDurability}
      },</#if>
      "minecraft:max_stack_size": ${data.stackSize}
      <#if data.isFood>,
      "minecraft:use_modifiers": {
      	"use_duration": ${data.useDuration},
      	"movement_modifier": ${data.movementModifier}
      },
      "minecraft:food": {
        "nutrition": ${data.foodNutritionalValue},
        "saturation_modifier": ${data.foodSaturation},
        "can_always_eat": ${data.foodCanAlwaysEat}
        <#if !data.usingConvertsTo.isEmpty()>,
        "using_converts_to": "${mappedMCItemToRegistryNameNoTags(data.usingConvertsTo)}"
        </#if>
      },
      "minecraft:use_animation": "${data.animation}",
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