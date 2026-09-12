<#-- @formatter:off -->
{
	"format_version": "1.10.0",
	"minecraft:client_entity": {
      "description": {
        "identifier": "${modid}:${registryname}",
        "textures": {
          "default": "textures/entities/${data.modelTexture}"
        },
        <#if data.hasSpawnEgg>
        "spawn_egg": {
          "base_color": "${thelper.colorToHexString(data.spawnEggBaseColor)}",
          "overlay_color": "${thelper.colorToHexString(data.spawnEggDotColor)}"
        },
        </#if>
        "materials": {
          "default": "entity_alphatest"
        },
        "geometry": {
          "default": "geometry.${data.modelName}"
        },
        "render_controllers": [ "controller.render.default" ]
      }
    }
}
<#-- @formatter:on -->