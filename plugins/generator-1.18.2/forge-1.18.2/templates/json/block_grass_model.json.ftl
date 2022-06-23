<#include "../textures.ftl">
{
  "parent": "block/grass_block",
  "textures": {
    "bottom": "${mappedSingleTexture(data.texture, "blocks", modid)}",
    "top": "${mappedElseTexture(data.textureTop, data.texture, "blocks", modid)}",
    "side": "${mappedElseTexture(data.textureFront, data.texture, "blocks", modid)}",
    "overlay": "${mappedElseTexture(data.textureLeft, data.texture, "blocks", modid)}",
    "particle": "${mappedElseTexture(data.particleTexture, data.texture, "blocks", modid)}"
  }
}