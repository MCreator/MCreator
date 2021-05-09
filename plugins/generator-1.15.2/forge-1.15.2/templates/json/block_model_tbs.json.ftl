<#include "../textures.ftl">
{
    "parent": "block/${var_model}",
    "textures": {
        <#if data.particleTexture?has_content>
        "particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}",
        </#if>
        "bottom": "${mappedSingleTexture(data.texture, "blocks", modid)}",
        <#if data.textureTop?has_content>
        "top": "${mappedSingleTexture(data.textureTop, "blocks", modid)}",
        <#else>
        "top": "${mappedSingleTexture(data.texture, "blocks", modid)}",
        </#if>
        <#if data.textureFront?has_content>
        "side": "${mappedSingleTexture(data.textureFront, "blocks", modid)}"
        <#else>
        "side": "${mappedSingleTexture(data.texture, "blocks", modid)}"
        </#if>
    }
}