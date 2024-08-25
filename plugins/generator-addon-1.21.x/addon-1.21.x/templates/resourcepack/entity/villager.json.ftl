<#-- @formatter:off -->
{
	"format_version": "1.8.0",
	"minecraft:client_entity": {
      "description": {
        "identifier": "${modid}:${registryname}",
        "textures": {
          "default": "textures/entities/${data.mobModelTexture}",
          "farmer": "textures/entities/${data.mobModelTexture}",
          "librarian": "textures/entities/${data.mobModelTexture}",
          "priest": "textures/entities/${data.mobModelTexture}",
          "smith": "textures/entities/${data.mobModelTexture}",
          "butcher": "textures/entities/${data.mobModelTexture}"
        },
        <#if data.hasSpawnEgg>
        "spawn_egg": {
          "base_color": "${thelper.colorToHexString(data.spawnEggBaseColor)}",
          "overlay_color": "${thelper.colorToHexString(data.spawnEggDotColor)}"
        },
        </#if>
        "materials": {
          "default": "villager"
        },
        "geometry": {
          "default": "geometry.villager"
        },
        "scripts": {
          "scale": "0.9375"
        },
        "animations": {
          "general": "animation.villager.general.v1.0",
          "look_at_target": "animation.common.look_at_target",
          "move": "animation.villager.move",
          "baby_transform": "animation.villager.baby_transform"
        },
        "animation_controllers": [
          { "general": "controller.animation.villager.general" },
          { "move": "controller.animation.villager.move" },
          { "baby": "controller.animation.villager.baby" }
        ],
        "render_controllers": [ "controller.render.villager.v1.0" ]
      }
    }
}
<#-- @formatter:on -->