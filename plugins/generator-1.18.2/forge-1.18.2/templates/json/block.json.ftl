<#include "../textures.ftl">
{
    "parent": "block/${var_model}",
    "textures": {
        "${var_txname}": "${mappedSingleTexture(data.texture, "blocks", modid)}",
        "particle": "${mappedElseTexture(data.particleTexture, data.texture, "blocks", modid)}"
    }
}