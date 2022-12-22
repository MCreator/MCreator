{
<#if var_item?? && (var_item=="helmet" || var_item=="body" || var_item=="leggings" || var_item=="boots")>
  "parent": "${modid}:custom/${data.getItemCustomModelNameFor(var_item)}",
<#else>
  "parent": "${modid}:custom/${data.customModelName.split(":")[0]}",
</#if>
  "textures": {
    <#if var_item?? && var_item=="helmet">
        <@textures data.getHelmetTextureMap()/>
    <#elseif var_item?? && var_item=="body">
        <@textures data.getBodyTextureMap()/>
    <#elseif var_item?? && var_item=="leggings">
        <@textures data.getLeggingsTextureMap()/>
    <#elseif var_item?? && var_item=="boots">
        <@textures data.getBootsTextureMap()/>
    <#else>
        <@textures data.getTextureMap()/>
    </#if>
<#if var_item?? && var_item=="helmet">
    "particle": "${modid}:items/${data.textureHelmet}"
<#elseif var_item?? && var_item=="body">
    "particle": "${modid}:items/${data.textureBody}"
<#elseif var_item?? && var_item=="leggings">
    "particle": "${modid}:items/${data.textureLeggings}"
<#elseif var_item?? && var_item=="boots">
    "particle": "${modid}:items/${data.textureBoots}"
<#else>
    "particle": "${modid}:items/${data.texture}"
</#if>
  }
}

<#macro textures textureMap>
    <#if textureMap??>
        <#list textureMap.entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
</#macro>