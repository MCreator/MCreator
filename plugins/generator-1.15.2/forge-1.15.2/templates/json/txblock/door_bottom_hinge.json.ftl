<#include "../textures.ftl">
{
    "parent": "block/door_bottom_rh",
    "textures": {
        <#if data.particleTexture?has_content>
        "particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}",
        </#if>
        "bottom": "${mappedSingleTexture(data.texture, "blocks", modid)}",
        <#if data.textureTop>
        "top": "${mappedSingleTexture(data.textureTop, "blocks", modid)}"
        <#else>
        "top": "${mappedSingleTexture(data.texture, "blocks", modid)}"
        </#if>
    }
}
