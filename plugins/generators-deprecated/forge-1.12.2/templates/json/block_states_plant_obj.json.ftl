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
    "type=dandelion": {
      "model": "${modid}:${data.customModelName.split(":")[0]}.obj"
    },
    "inventory": [
      {
        "transform": "forge:default-item"
      }
    ]
  }
}