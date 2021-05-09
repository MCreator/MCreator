<#include "../textures.ftl">
{
    "parent": "block/${var_model}",
    "textures": {
        <#if data.textureBottom?has_content>
        "cross": "${mappedSingleTexture(data.textureBottom, "blocks", modid)}",
        <#else>
        "cross": "${mappedSingleTexture(data.texture, "blocks", modid)}",
        </#if>
        <#if data.particleTexture?has_content>
        "particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}"
        <#else>
        <#if data.textureBottom?has_content>
        "particle": "${mappedSingleTexture(data.textureBottom, "blocks", modid)}"
        <#else>
        "particle": "${mappedSingleTexture(data.texture, "blocks", modid)}"
        </#if>
        </#if>
    }
}