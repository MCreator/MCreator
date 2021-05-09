<#include "../textures.ftl">
{
    "parent": "item/generated",
    "textures": {
        <#if var_item?? && var_item=="helmet">
            "layer0": "${mappedSingleTexture(data.textureHelmet, "items", modid)}"
        <#elseif var_item?? && var_item=="body">
            "layer0": "${mappedSingleTexture(data.textureBody, "items", modid)}"
        <#elseif var_item?? && var_item=="leggings">
            "layer0": "${mappedSingleTexture(data.textureLeggings, "items", modid)}"
        <#elseif var_item?? && var_item=="boots">
            "layer0": "${mappedSingleTexture(data.textureBoots, "items", modid)}"
        <#else>
            "layer0": "${mappedSingleTexture(data.texture, "items", modid)}"
        </#if>
    }
}