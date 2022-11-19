{
  "forge_marker": 1,
  "parent": "forge:item/default",
  "loader": "forge:obj",
<#if var_item?? && var_item=="helmet">
  "model": "${modid}:models/item/${data.helmetItemCustomModelName.split(":")[0]}.obj",
<#elseif var_item?? && var_item=="body">
  "model": "${modid}:models/item/${data.bodyItemCustomModelName.split(":")[0]}.obj",
<#elseif var_item?? && var_item=="leggings">
  "model": "${modid}:models/item/${data.LeggingsItemCustomModelName.split(":")[0]}.obj",
<#elseif var_item?? && var_item=="boots">
  "model": "${modid}:models/item/${data.bootsItemCustomModelName.split(":")[0]}.obj",
<#else>
  "model": "${modid}:models/item/${data.customModelName.split(":")[0]}.obj",
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