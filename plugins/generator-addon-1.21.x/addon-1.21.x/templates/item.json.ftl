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
      <#if data.hasGlint>"minecraft:foil": true</#if>,
      "minecraft:max_stack_size": ${data.stackSize},
      "minecraft:max_damage": ${data.maxDurability},
      <#if data.isFood>
      "minecraft:use_duration": ${data.useDuration},
      "minecraft:food": {
        "nutrition": ${data.foodNutritionalValue},
        "nutritionalValue": "${thelper.mapToString(data.foodSaturation, 0, 1.2, "poor", "low", "normal", "good", "high", "supernatural")}",
        "saturation_modifier": "low",
        "can_always_eat": ${data.foodCanAlwaysEat},
        <#if data.foodIsMeat>"is_meat": true</#if>
      },
      </#if>
      "minecraft:creative_category": "Items"
    }
  }
}
<#-- @formatter:on -->