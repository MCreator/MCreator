<#-- @formatter:off -->
{
	"format_version": "1.8.0",
	"minecraft:client_entity": {
      "description": {
        "identifier": "${modid}:${registryname}",
        "textures": {
          "default": "textures/${data.mobModelTexture}"
        },
        <#if data.hasSpawnEgg>
        "spawn_egg": {
          "base_color": "${thelper.colorToHexString(data.spawnEggBaseColor)}",
          "overlay_color": "${thelper.colorToHexString(data.spawnEggDotColor)}"
        },
        </#if>
        "materials": {
          "default": "silverfish",
          "body_layer": "silverfish_layers"
        },
        "geometry": {
          "default": "geometry.silverfish"
        },
        "animations": {
          "move": "animation.silverfish.move"
        },
        "animation_controllers": [
          { "move": "controller.animation.silverfish.move" }
        ],
        "render_controllers": [ "controller.render.silverfish" ]
      }
    }
}
<#-- @formatter:on -->