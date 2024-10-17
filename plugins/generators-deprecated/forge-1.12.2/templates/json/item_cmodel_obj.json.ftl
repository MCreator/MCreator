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
      "inventory": [
        {
            <#if var_type?? && var_type=="tool">
            "transform": "forge:default-tool"
            <#else>
            "transform": "forge:default-item"
            </#if>
        }
      ]
    }
}