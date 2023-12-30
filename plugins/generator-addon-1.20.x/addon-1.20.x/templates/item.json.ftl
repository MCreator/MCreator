<#-- @formatter:off -->
{
  "format_version": "1.12",
  "minecraft:item": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "register_to_creative_menu": true,
      "is_experimental": false
    },

    "components": {
      <#if data.glowCondition?? && data.glowCondition.getFixedValue()>"minecraft:foil": true,</#if>
      "minecraft:max_stack_size": ${data.stackSize},
      "minecraft:max_damage": ${data.damageCount},
      <#if data.isFood>
      "minecraft:use_duration": ${data.useDuration},
      "minecraft:food": {
        "nutrition": ${data.nutritionalValue},
        "nutritionalValue": "${thelper.mapToString(data.saturation, 0, 1.2, "poor", "low", "normal", "good", "high", "supernatural")}",
        "saturation_modifier": "low",
        "can_always_eat": ${data.isAlwaysEdible},
        "is_meat": ${data.isMeat}
      },
      </#if>
      "minecraft:creative_category": "Items"
    }
  }
}
<#-- @formatter:on -->