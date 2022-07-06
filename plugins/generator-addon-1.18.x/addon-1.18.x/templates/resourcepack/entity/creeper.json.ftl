<#-- @formatter:off -->
{
	"format_version": "1.8.0",
	"minecraft:client_entity": {
      "description": {
        "identifier": "${modid}:${registryname}",
        "textures": {
          "default": "textures/entities/${data.mobModelTexture}",
          "charged": "textures/entities/${data.mobModelTexture}"
        },
        <#if data.hasSpawnEgg>
        "spawn_egg": {
          "base_color": "${thelper.colorToHexString(data.spawnEggBaseColor)}",
          "overlay_color": "${thelper.colorToHexString(data.spawnEggDotColor)}"
        },
        </#if>
        "materials": {
          "default": "creeper",
          "charged": "charged_creeper"
        },
        "geometry": {
          "default": "geometry.creeper",
          "charged": "geometry.creeper.charged"
        },
        "spawn_egg": {
          "texture": "spawn_egg",
          "texture_index": 6
        },
        "scripts": {
          "pre_animation": [
            "variable.wobble = Math.sin(query.swell_amount * 5730) * query.swell_amount * 0.01 + 1.0;",
            "variable.swelling_scale1 = (Math.pow(Math.clamp(query.swell_amount, 0.0, 1.0), 4.0) * 0.4 + 1.0) * variable.wobble;",
            "variable.swelling_scale2 = (Math.pow(Math.clamp(query.swell_amount, 0.0, 1.0), 4.0) * 0.1 + 1.0) / variable.wobble;",
            "variable.leg_rot = Math.cos(query.modified_distance_moved * 38.17326) * 80.22 * query.modified_move_speed;",
            "variable.flash = Math.mod(Math.Round(query.swell_amount * 10.0), 2.0);"
          ]
        },
        "animations": {
          "creeper_head": "animation.common.look_at_target",
          "creeper_legs": "animation.creeper.legs",
          "creeper_swelling": "animation.creeper.swelling"
        },
        "animation_controllers": [
          { "creeper_head": "controller.animation.creeper.head" },
          { "creeper_legs": "controller.animation.creeper.legs" },
          { "creeper_swelling": "controller.animation.creeper.swelling" }
        ],
        "render_controllers": [
          "controller.render.creeper",
          "controller.render.creeper_armor"
        ]
      }
    }
}
<#-- @formatter:on -->