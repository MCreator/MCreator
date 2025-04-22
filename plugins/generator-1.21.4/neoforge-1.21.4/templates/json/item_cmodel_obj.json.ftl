{
  "forge_marker": 1,
  "parent": "neoforge:item/default",
  "loader": "neoforge:obj",
<#if var_item??> <#-- used by armor where item type is specified (helmet, body, ...) -->
  "model": "${modid}:models/item/${data.getItemCustomModelNameFor(var_item)}.obj",
  "textures": {
    <@textures data.getItemModelTextureMap(var_item)/>
    "particle": "${data.getItemTextureFor(var_item).format("%s:item/%s")}"
<#else>
  "model": "${modid}:models/item/${data.customModelName.split(":")[0]}.obj",
  "textures": {
    <@textures data.getTextureMap()/>
    "particle": "${data.texture.format("%s:item/%s")}"
</#if>
  }
}

<#macro textures textureMap>
    <#if textureMap??>
        <#list textureMap.entrySet() as texture>
            "${texture.getKey()}": "${texture.getValue().format("%s:block/%s")}",
        </#list>
    </#if>
</#macro>