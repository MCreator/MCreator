<#include "../textures.ftl">
{
    "parent": "item/generated",
    "textures": {
        <#if data.itemTexture?has_content>
        "layer0": "${mappedSingleTexture(data.itemTexture, "items", modid)}"
        <#else>
        "layer0": "${mappedSingleTexture(data.texture, "blocks", modid)}"
        </#if>
    }
}