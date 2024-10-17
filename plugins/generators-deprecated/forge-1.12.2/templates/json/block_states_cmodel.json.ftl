{
    "forge_marker": 1,
    "defaults": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
      <#if data.getTextureMap()??>
        ,
        "custom": {
          "flip-v": true
        },
        "textures": {
        <#list data.getTextureMap().entrySet() as texture>
              "#${texture.getKey()}": "${modid}:blocks/${texture.getValue()}"<#if texture?has_next>,</#if>
        </#list>
        }
      </#if>
    },
    "variants": {
      "normal": {
        "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
      },
      "facing=north": {
        "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
      },
      "facing=east": {
        "model": "${modid}:${data.customModelName.split(":")[0]}.obj",
        "y": 90
      },
      "facing=south": {
        "model": "${modid}:${data.customModelName.split(":")[0]}.obj",
        "y": 180
      },
      "facing=west": {
        "model": "${modid}:${data.customModelName.split(":")[0]}.obj",
        "y": 270
      },
      "facing=up": {
        "model": "${modid}:${data.customModelName.split(":")[0]}.obj",
        "x": 90
      },
      "facing=down": {
        "model": "${modid}:${data.customModelName.split(":")[0]}.obj",
        "x": 270
      },
      "inventory": [
        {
          "transform": "forge:default-item"
        }
      ]
    }
}