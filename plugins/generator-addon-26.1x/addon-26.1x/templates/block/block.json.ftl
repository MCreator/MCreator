<#-- @formatter:off -->
<#assign localScripts = data.localScripts?map(s -> generator.getResourceLocationForModElement(s))>
{
  "format_version": "1.21.120",
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
      "minecraft:geometry": <@regular_model/>,
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
      <#if data.hasParticleTexture()>
      "minecraft:destruction_particles": {
        "texture": "${modid}_${registryname}_destruction_particles"
        <#if data.particleTintMethod != "(none)">,
        "tint_method": "${data.particleTintMethod}"
        </#if>
        <#if data.particleCount != 100>,
        "particle_count": ${data.particleCount}
        </#if>
      },
      </#if>
      <#if data.flowerPottable>
      "minecraft:flower_pottable": {},
       <#if data.hasCustomPottedModel() || data.pottedRenderType() == 11 || data.pottedRenderType() == 12 || data.hasPottedTexture()>
		  "minecraft:embedded_visual": {
		    "geometry":
		      <#if data.hasCustomPottedModel()>
		      "geometry.${data.getPottedModel().getReadableName()}"
		      <#elseif data.pottedRenderType() == 11>
		      "minecraft:geometry.cross"
		      <#elseif data.pottedRenderType() == 12>
		      "minecraft:geometry.full_block"
		      <#elseif data.hasCustomModel()>
		      "geometry.${data.getModel().getReadableName()}"
		      <#else>
		      <@regular_model/>
		      </#if>,
	    	  "material_instances": {
	  	       "*": <@material_face "potted" data.pottedRenderType() == 11 data.pottedRenderType() == 11/>
		      }
	      },
       </#if>
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

<#macro regular_model>
  <#if data.hasCustomModel()>
  "geometry.${data.getModel().getReadableName()}"
  <#elseif data.renderType() == 11>
  "minecraft:geometry.cross"
  <#else>
  "minecraft:geometry.full_block"
  </#if>
</#macro>

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