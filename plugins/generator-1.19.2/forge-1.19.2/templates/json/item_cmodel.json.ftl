{
<#if var_item??> <#-- used by armor where item type is specified (helmet, body, ...) -->
  "parent": "${modid}:custom/${data.getItemCustomModelNameFor(var_item)}",
  "textures": {
    <@textures data.getItemModelTextureMap(var_item)/>
    "particle": "${modid}:items/${data.getItemTextureFor(var_item)}"
  }
<#else>
  "parent": "${modid}:custom/${data.customModelName.split(":")[0]}",
  "textures": {
    <@textures data.getTextureMap()/>
    "particle": "${modid}:items/${data.texture}"
  }<#if data.getModElement().getTypeString() == "tool" && data.toolType == "Shield">,
  "overrides": [
      {
          "predicate": {
              "blocking": 1
          },
          "model": "${modid}:item/${registryname}_blocking"
      }
  ]
</#if>
</#if>
}

<#macro textures textureMap>
    <#if textureMap??>
        <#list textureMap.entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
</#macro>