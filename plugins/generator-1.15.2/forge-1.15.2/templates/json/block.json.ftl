<#include "../textures.ftl">
{
    "parent": "block/${var_model}",
    "textures": {
        "${var_txname}": "${mappedSingleTexture(data.texture, "blocks", modid)}",
        <#if data.particleTexture?has_content>
        "particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}"
        <#else>
        "particle": "${mappedSingleTexture(data.texture, "blocks", modid)}"
        </#if>
    }
}