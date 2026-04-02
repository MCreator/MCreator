<#-- @formatter:off -->
{
	"format_version": "1.10.0",
	"minecraft:client_entity": {
      "description": {
        "identifier": "${modid}:${registryname}",
        "textures": {
          "default": "textures/entities/${data.mobModelTexture}",
          "saddled": "textures/entities/${data.mobModelTexture}"
        },
        <#if data.hasSpawnEgg>
        "spawn_egg": {
          "base_color": "${thelper.colorToHexString(data.spawnEggBaseColor)}",
          "overlay_color": "${thelper.colorToHexString(data.spawnEggDotColor)}"
        },
        </#if>
        "materials": {
          "default": "pig"
        },
        "geometry": {
          "default": "geometry.pig"
        },
        "animations": {
          "setup": "animation.pig.setup.v1.0",
          "walk": "animation.quadruped.walk",
          "look_at_target": "animation.common.look_at_target",
          "baby_transform": "animation.pig.baby_transform"
        },
        "scripts": {
          "animate": [
            "setup",
            { "walk": "query.modified_move_speed" },
            "look_at_target",
            { "baby_transform": "query.is_baby" }
          ]
        },
        "render_controllers": [ "controller.render.pig" ]
      }
    }
}
<#-- @formatter:on -->