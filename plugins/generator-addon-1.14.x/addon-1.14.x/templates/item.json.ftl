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
      <#if data.hasGlow>"minecraft:foil": true,</#if>
      "minecraft:max_stack_size": ${data.stackSize},
      "minecraft:max_damage": ${data.damageCount}
    }
  }
}
<#-- @formatter:on -->