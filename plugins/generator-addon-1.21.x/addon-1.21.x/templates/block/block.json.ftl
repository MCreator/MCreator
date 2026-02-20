<#-- @formatter:off -->
<#assign localScripts = data.localScripts?map(s -> generator.getResourceLocationForModElement(s))>
{
  "format_version": "1.21.40",
  "minecraft:block": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "menu_category": {
        "category": "<#if data.enableCreativeTab>${generator.map(data.creativeTab, "tabs")}<#else>none</#if>"
        <#if data.isHiddenInCommands>,"is_hidden_in_commands": true</#if>
      }
      <#if data.rotationMode != 0>,
      "traits": {
        <#if data.rotationMode == 1>
        "minecraft:placement_direction": {
          "enabled_states": ["minecraft:cardinal_direction"]
        }
        <#elseif data.rotationMode == 2>
        "minecraft:placement_direction": {
          "enabled_states": ["minecraft:facing_direction"]
        }
        <#elseif data.rotationMode == 3 || data.rotationMode == 4>
        "minecraft:placement_position": {
          "enabled_states": [ "minecraft:block_face" ]
        }
        </#if>
      }
      </#if>
    },
    "components": {
      "minecraft:geometry": <#if data.hasCustomModel()>"geometry.${data.getModel().getReadableName()}"<#else>"minecraft:geometry.full_block"</#if>,
      "minecraft:material_instances": {
        <#if data.hasCustomModel()>
		"*": <@material_face/>
		<#else>
        "up": <@material_face "up"/>,
        "down": <@material_face "down"/>,
        "north": <@material_face "north"/>,
        "south": <@material_face "south"/>,
        "east": <@material_face "east"/>,
        "west": <@material_face "west"/>
		</#if>
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
      }<#if localScripts?has_content>,</#if>
      <#list localScripts as script>
      "${script}": {}<#sep>,
      </#list>
    }
    <#if data.rotationMode != 0>,
    "permutations": [
      <#if data.rotationMode = 1 || data.rotationMode = 2>
        <#if data.rotationMode = 2>
        {
          "condition": "${rotationCondition()} == 'down'",
          "components": { "minecraft:transformation": { "rotation": [-90, 0, 0] } }
        },
        {
          "condition": "${rotationCondition()} == 'up'",
          "components": { "minecraft:transformation": { "rotation": [90, 0, 0] } }
        },
        </#if>
        {
          "condition": "${rotationCondition()} == 'north'",
          "components": { "minecraft:transformation": { "rotation": [0, 0, 0] } }
        },
        {
          "condition": "${rotationCondition()} == 'west'",
          "components": { "minecraft:transformation": { "rotation": [0, 90, 0] } }
        },
        {
          "condition": "${rotationCondition()} == 'south'",
          "components": { "minecraft:transformation": { "rotation": [0, 180, 0] } }
        },
        {
          "condition": "${rotationCondition()} == 'east'",
          "components": { "minecraft:transformation": { "rotation": [0, -90, 0] } }
        }
      <#elseif data.rotationMode = 3>
        {
          "condition": "${rotationCondition()} == 'down'",
          "components": { "minecraft:transformation": { "rotation": [90, 0, 0] } }
        },
        {
          "condition": "${rotationCondition()} == 'up'",
          "components": { "minecraft:transformation": { "rotation": [-90, 0, 0] } }
        },
        {
          "condition": "${rotationCondition()} == 'south'",
          "components": { "minecraft:transformation": { "rotation": [0, 0, 0] } }
        },
        {
          "condition": "${rotationCondition()} == 'east'",
          "components": { "minecraft:transformation": { "rotation": [0, 90, 0] } }
        },
        {
          "condition": "${rotationCondition()} == 'north'",
          "components": { "minecraft:transformation": { "rotation": [0, 180, 0] } }
        },
        {
          "condition": "${rotationCondition()} == 'west'",
          "components": { "minecraft:transformation": { "rotation": [0, -90, 0] } }
        }
      <#elseif data.rotationMode = 4>
        {
          "condition": "${rotationCondition()} == 'west' || ${rotationCondition()} == 'east'",
          "components": { "minecraft:transformation": { "rotation": [0, 0, 90] } }
        },
        {
          "condition": "${rotationCondition()} == 'down' || ${rotationCondition()} == 'up'",
          "components": { "minecraft:transformation": { "rotation": [0, 0, 0] } }
        },
        {
          "condition": "${rotationCondition()} == 'north' || ${rotationCondition()} == 'south'",
          "components": { "minecraft:transformation": { "rotation": [90, 0, 0] } }
        }
      </#if>
    ]
    </#if>
  }
}

<#function rotationCondition>
  <#if data.rotationMode == 1>
    <#return "q.block_state('minecraft:cardinal_direction')">
  <#elseif data.rotationMode == 2>
    <#return "q.block_state('minecraft:facing_direction')">
  <#elseif data.rotationMode == 3 || data.rotationMode == 4>
    <#return "q.block_state('minecraft:block_face')">
  </#if>
</#function>

<#macro material_face suffix="">
{
  "texture": "${modid}_${registryname}<#if suffix?has_content>_${suffix}</#if>"
  <#if data.renderMethod != "opaque">,"render_method": "${data.renderMethod}"</#if>
  <#if data.tintMethod != "(none)">,"tint_method": "${data.tintMethod}"</#if>
}
</#macro>

<#-- @formatter:on -->