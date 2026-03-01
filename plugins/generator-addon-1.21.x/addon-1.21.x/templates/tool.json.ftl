<#-- @formatter:off -->
{
  "format_version": "1.21.50",
  "minecraft:item": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "menu_category": {
        "category": "Equipment"
      }
    },

    "components": {
      <#if data.glowCondition?? && data.glowCondition.getFixedValue()>"minecraft:glint": true,</#if>
      "minecraft:max_stack_size": 1,
      "minecraft:durability": {
        "max_durability": ${data.usageCount}
      },
      "minecraft:damage": ${[data.damageVsEntity?round, 255]?min},
      "minecraft:stacked_by_data": true,
      "minecraft:hand_equipped": true,
      "minecraft:icon": "${registryname}"
    }
  }
}
<#-- @formatter:on -->