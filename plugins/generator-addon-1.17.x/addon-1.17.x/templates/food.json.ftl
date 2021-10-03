<#-- @formatter:off -->
<#assign saturations = []>
{
  "format_version": "1.12",
  "minecraft:item": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "register_to_creative_menu": true
    },

    "components": {
      <#if data.hasGlow>"minecraft:foil": true,</#if>
      "minecraft:max_stack_size": ${data.stackSize},
      "minecraft:use_duration": ${data.eatingSpeed},
      "minecraft:food": {
        "nutrition": ${data.nutritionalValue},
        "nutritionalValue": "${thelper.mapToString(data.saturation, 0, 1.2,
                "poor", "low", "normal", "good", "high", "supernatural")}",
        "saturation_modifier": "low",
        "can_always_eat": ${data.isAlwaysEdible},
        "is_meat": ${data.forDogs}
      }
    }
  }
}
<#-- @formatter:on -->