<#-- @formatter:off -->
{
	"format_version": "1.8.0",
	"minecraft:client_entity": {
      "description": {
        "identifier": "${modid}:${registryname}",
        "textures": {
          "default": "textures/${data.mobModelTexture}",
          "shooting": "textures/${data.mobModelTexture}"
        },
        <#if data.hasSpawnEgg>
        "spawn_egg": {
          "base_color": "${thelper.colorToHexString(data.spawnEggBaseColor)}",
          "overlay_color": "${thelper.colorToHexString(data.spawnEggDotColor)}"
        },
        </#if>
        "materials": {
          "default": "ghast"
        },
        "geometry": {
          "default": "geometry.ghast"
        },
        "animations": {
          "move": "animation.ghast.move",
          "scale": "animation.ghast.scale"
        },
        "scripts": {
          "pre_animation": [
            "variable.ischarging = 0;"
          ]
        },
        "animation_controllers": [
          { "move": "controller.animation.ghast.move" },
          { "scale": "controller.animation.ghast.scale" }
        ],
        "render_controllers": [ "controller.render.ghast" ]
      }
    }
}
<#-- @formatter:on -->