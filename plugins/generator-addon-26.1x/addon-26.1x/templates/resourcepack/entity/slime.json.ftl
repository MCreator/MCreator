<#-- @formatter:off -->
{
	"format_version": "1.8.0",
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
          "default": "slime",
          "outer": "slime_outer"
        },
        "geometry": {
          "default": "geometry.slime",
          "armor": "geometry.slime.armor"
        },
        "scripts": {
          "pre_animation": [
            "variable.squish_factor = (query.previous_squish_value + (query.current_squish_value - query.previous_squish_value) * query.frame_alpha);",
            "variable.bounce = 1 / ((variable.squish_factor / (2 * 0.5 + 1)) + 1);",
            "variable.horizontal_scale_amount = variable.bounce * 2;",
            "variable.vertical_scale_amount = (1 / variable.bounce) * 2;"
          ],
          "scaleX": "variable.horizontal_scale_amount",
          "scaleY": "variable.vertical_scale_amount",
          "scaleZ": "variable.horizontal_scale_amount"
        },
        "render_controllers": [
          "controller.render.slime",
          "controller.render.slime_armor"
        ]
      }
    }
}
<#-- @formatter:on -->