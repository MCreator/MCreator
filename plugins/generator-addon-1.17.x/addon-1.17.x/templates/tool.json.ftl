<#-- @formatter:off -->
{
  "format_version": "1.12",
  "minecraft:item": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "register_to_creative_menu": true
    },

    "components": {
      <#if data.hasGlow>"minecraft:foil": true,</#if>
      "minecraft:max_stack_size": 1,
      "minecraft:max_damage": ${data.usageCount},
      "minecraft:stacked_by_data": true,
      "minecraft:hand_equipped": true
    }
  }
}
<#-- @formatter:on -->