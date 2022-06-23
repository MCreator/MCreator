<#include "../textures.ftl">
{
  "parent": "block/cube",
  "textures": {
  "down": "${mappedSingleTexture(data.texture, "blocks", modid)}",
  "up": "${mappedElseTexture(data.textureTop, data.texture, "blocks", modid)}",
  "north": "${mappedElseTexture(data.textureFront, data.texture, "blocks", modid)}",
  "east": "${mappedElseTexture(data.textureLeft, data.texture, "blocks", modid)}",
  "south": "${mappedElseTexture(data.textureBack, data.texture, "blocks", modid)}",
  "west": "${mappedElseTexture(data.textureRight, data.texture, "blocks", modid)}",
  "particle": "${mappedElseTexture(data.particleTexture, data.texture, "blocks", modid)}"
  }
}