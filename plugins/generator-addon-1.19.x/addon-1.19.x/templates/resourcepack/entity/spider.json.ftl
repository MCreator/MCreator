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
          "default": "spider",
          "invisible": "spider_invisible"
        },
        "geometry": {
          "default": "geometry.spider"
        },
        "animations": {
          "default_leg_pose": "animation.spider.default_leg_pose",
          "look_at_target": "animation.spider.look_at_target",
          "walk": "animation.spider.walk"
        },
        "animation_controllers": [
          { "move": "controller.animation.spider.move" }
        ],
        "render_controllers": [ "controller.render.spider" ]
      }
    }
}
<#-- @formatter:on -->