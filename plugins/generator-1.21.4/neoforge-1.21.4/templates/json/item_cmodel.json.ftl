{
<#if var_item??> <#-- used by armor where item type is specified (helmet, body, ...) -->
  "parent": "${modid}:custom/${data.getItemCustomModelNameFor(var_item)}",
  "textures": {
    <@textures data.getItemModelTextureMap(var_item)/>
    "particle": "${data.getItemTextureFor(var_item).format("%s:item/%s")}"
  }
<#else>
  "parent": "${modid}:custom/${data.customModelName.split(":")[0]}",
  "textures": {
    <@textures data.getTextureMap()/>
    "particle": "${data.texture.format("%s:item/%s")}"
  }
</#if>
}

<#macro textures textureMap>
    <#if textureMap??>
        <#list textureMap.entrySet() as texture>
            "${texture.getKey()}": "${texture.getValue().format("%s:block/%s")}",
        </#list>
    </#if>
</#macro>