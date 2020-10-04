<#-- @formatter:off -->
{
	"format_version": "1.10.0",
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
        "materials": { "default": "cow" },
        "geometry": {
          "default": "geometry.cow"
        },
        "animations": {
          "setup": "animation.cow.setup.v1.0",
          "walk": "animation.quadruped.walk",
          "look_at_target": "animation.common.look_at_target",
          "baby_transform": "animation.cow.baby_transform"
        },
        "scripts": {
          "animate": [
            "setup",
            { "walk": "query.modified_move_speed" },
            "look_at_target",
            { "baby_transform": "query.is_baby" }
          ]
        },
        "render_controllers": [ "controller.render.cow" ]
      }
    }
}
<#-- @formatter:on -->