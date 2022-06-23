<#include "../textures.ftl">
{
    "parent": "block/${var_model}",
    "textures": {
        "cross": "${mappedElseTexture(data.textureBottom, data.texture, "blocks", modid)}",
        "particle": "${mappedDoubleElseTexture(data.particleTexture, data.textureBottom, data.texture, "blocks", modid)}"
    }
}