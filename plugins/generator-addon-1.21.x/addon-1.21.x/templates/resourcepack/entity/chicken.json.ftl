<#-- @formatter:off -->
{
	"format_version": "1.10.0",
	"minecraft:client_entity": {
      "description": {
        "identifier": "${modid}:${registryname}",
        "textures": {
          "default": "textures/entities/${data.mobModelTexture}"
        },
        <#if data.hasSpawnEgg>
        "spawn_egg": {
          "base_color": "${thelper.colorToHexString(data.spawnEggBaseColor)}",
          "overlay_color": "${thelper.colorToHexString(data.spawnEggDotColor)}"
        },
        </#if>
        "materials": {
          "default": "chicken",
          "legs": "chicken_legs"
        },
        "geometry": {
          "default": "geometry.chicken"
        },
        "animations": {
          "move": "animation.chicken.move",
          "general": "animation.chicken.general",
          "look_at_target": "animation.common.look_at_target"
        },
        "scripts": {
          "pre_animation": [
            "variable.wing_flap = Math.cos(query.life_time * 720.0) * 50.0 + 50.0;"
          ],
          "animate": [
            "general",
            { "move": "query.modified_move_speed" }
          ]
        },
        "render_controllers": [ "controller.render.chicken" ]
      }
    }
}
<#-- @formatter:on -->