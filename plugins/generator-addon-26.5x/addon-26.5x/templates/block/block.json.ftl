<#-- @formatter:off -->
<#assign localScripts = data.localScripts?map(s -> generator.getResourceLocationForModElement(s))>
{
  "format_version": "1.21.40",
  "minecraft:block": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "menu_category": {
        "category": "<#if data.enableCreativeTab>${data.creativeTab}<#else>none</#if>"
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
      "minecraft:geometry":
        <#if data.hasCustomModel()>
          "geometry.${data.getModel().getReadableName()}"
        <#elseif data.renderType() == 11>
          "minecraft:geometry.cross"
        <#else>
          "minecraft:geometry.full_block"
        </#if>,
      "minecraft:material_instances": {
        <#if data.hasOneTexture()>
		"*": <@material_face "" data.renderType() == 11 data.renderType() == 11/>
		<#else>
        "up": <@material_face "up"/>,
        "down": <@material_face "down"/>,
        "north": <@material_face "north"/>,
        "south": <@material_face "south"/>,
        "east": <@material_face "east"/>,
        "west": <@material_face "west"/>
		</#if>
      },
      <#-- Boxes are emitted in the base orientation; minecraft:transformation permutations rotate them with the geometry -->
      <#if data.isNotColidable>
      "minecraft:collision_box": false,
      <#elseif !data.isFullCube()>
      "minecraft:collision_box": <@collisionBoxes data.positiveBoundingBoxes()/>,
      </#if>
      <#if !data.isFullCube()>
      "minecraft:selection_box": <#if data.getSelectionBox()??><@boxEntry data.getSelectionBox()/><#else>false</#if>,
      </#if>
      <#if (data.colorOnMap!"DEFAULT") != "DEFAULT">
      "minecraft:map_color": "${data.colorOnMap}",
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
      <#if data.friction != 0.4>
      "minecraft:friction": ${data.friction},
      </#if>
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

<#-- Bedrock box origin is relative to the bottom center of the block and its X axis points west, while MCreator boxes
     are relative to the bottom north-west corner with X pointing east, so X is mirrored and Z is only shifted -->
<#macro bedrockBox minX minY minZ maxX maxY maxZ>
{
  "origin": [${8 - maxX}, ${minY}, ${minZ - 8}],
  "size": [${maxX - minX}, ${maxY - minY}, ${maxZ - minZ}]
}
</#macro>

<#macro collisionBoxes boxes>
  <#if boxes?size == 0>
    false
  <#elseif boxes?size == 1>
    <@boxEntry boxes[0]/>
  <#else>
    [<#list boxes as box><@boxEntry box/><#sep>,</#list>]
  </#if>
</#macro>

<#macro boxEntry box>
  <@bedrockBox min(box.mx, box.Mx) min(box.my, box.My) min(box.mz, box.Mz) max(box.mx, box.Mx) max(box.my, box.My) max(box.mz, box.Mz)/>
</#macro>

<#function min(a, b)>
  <#return (a < b)?then(a, b)>
</#function>

<#function max(a, b)>
  <#return (a > b)?then(a, b)>
</#function>

<#macro material_face suffix="" disableAmbientOcclusion=false disableFaceDimming=false>
{
  "texture": "${modid}_${registryname}<#if suffix?has_content>_${suffix}</#if>"
  <#if data.renderMethod != "opaque">,"render_method": "${data.renderMethod}"</#if>
  <#if data.tintMethod != "(none)">,"tint_method": "${data.tintMethod}"</#if>
  <#if disableAmbientOcclusion>,"ambient_occlusion": false</#if>
  <#if disableFaceDimming>,"face_dimming": false</#if>
}
</#macro>

<#-- @formatter:on -->