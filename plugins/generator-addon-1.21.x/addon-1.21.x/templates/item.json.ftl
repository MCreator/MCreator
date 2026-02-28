<#-- @formatter:off -->
<#assign localScripts = data.localScripts?map(s -> generator.getResourceLocationForModElement(s))>
<#include "mcitems.ftl">
{
  "format_version": "1.21.90",
  "minecraft:item": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "menu_category": {
      	"category": "<#if data.enableCreativeTab>${generator.map(data.creativeTab, "tabs")}<#else>none</#if>"
      	<#if data.isHiddenInCommands>,"is_hidden_in_commands": true</#if>
      }
    },
    "components": {
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
      <#if data.stackSize lt 64>
      "minecraft:max_stack_size": ${data.stackSize},
      </#if>
      <#if data.blockToPlace?? && !data.blockToPlace.isEmpty()>
      "minecraft:block_placer": {
          "block": "${mappedMCItemToRegistryNameNoTags(data.blockToPlace)}",
          "use_on": [
            <#list data.blockPlaceableOn as block>
            "${mappedMCItemToRegistryNameNoTags(block)}"<#sep>,
            </#list>
          ]
      },
      </#if>
      <#if data.entityToPlace?? && !data.entityToPlace.isEmpty()>
      "minecraft:entity_placer": {
        "entity": "${generator.map(data.entityToPlace.getUnmappedValue(), "entities")}",
        "dispense_on": [
            <#list data.entityDispensableOn as block>
            "${mappedMCItemToRegistryNameNoTags(block)}"<#sep>,
            </#list>
        ],
        "use_on": [
            <#list data.entityPlaceableOn as block>
            "${mappedMCItemToRegistryNameNoTags(block)}"<#sep>,
            </#list>
        ]
      },
      </#if>
      <#if data.useDuration gt 0 || data.isFood>
      "minecraft:use_modifiers": {
      	"use_duration": ${data.useDuration},
      	"movement_modifier": ${data.movementModifier}
      },
      </#if>
      <#if data.isFood>
      "minecraft:food": {
        "nutrition": ${data.foodNutritionalValue},
        "saturation_modifier": ${data.foodSaturation},
        "can_always_eat": ${data.foodCanAlwaysEat}
        <#if data.usingConvertsTo?? && !data.usingConvertsTo.isEmpty()>,
        "using_converts_to": "${mappedMCItemToRegistryNameNoTags(data.usingConvertsTo)}"
        </#if>
      },
      "minecraft:use_animation": "${data.animation}",
      "minecraft:tags": {
      	"tags": [
      	  "minecraft:is_food"
      	]
      },
      </#if>
      "minecraft:icon": "${registryname}"<#if localScripts?has_content>,</#if>
      <#list localScripts as script>
      "${script}": {}<#sep>,
      </#list>
    }
  }
}
<#-- @formatter:on -->