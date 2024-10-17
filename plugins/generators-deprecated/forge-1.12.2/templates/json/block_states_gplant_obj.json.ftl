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
    "age=0": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=1": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=2": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=3": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=4": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=5": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=6": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=7": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=8": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=9": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=10": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=11": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=12": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=13": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=14": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "age=15": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "inventory": [
      {
        "transform": "forge:default-item"
      }
    ]
  }
}