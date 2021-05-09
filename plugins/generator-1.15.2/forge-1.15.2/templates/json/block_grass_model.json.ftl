<#include "../textures.ftl">
{
  "parent": "block/grass_block",
  "textures": {
    "bottom": "${mappedSingleTexture(data.texture, "blocks", modid)}",
    <#if data.textureTop?has_content>
    "top": "${mappedSingleTexture(data.textureTop, "blocks", modid)}",
    <#else>
    "top": "${mappedSingleTexture(data.texture, "blocks", modid)}",
    </#if>
    <#if data.textureFront?has_content>
    "side": "${mappedSingleTexture(data.textureFront, "blocks", modid)}",
    <#else>
    "side": "${mappedSingleTexture(data.texture, "blocks", modid)}",
    </#if>
    <#if data.textureLeft?has_content>
    "overlay": "${mappedSingleTexture(data.textureLeft, "blocks", modid)}",
    <#else>
    "overlay": "${mappedSingleTexture(data.texture, "blocks", modid)}",
    </#if>
    <#if data.particleTexture?has_content>
    "particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}"
    <#else>
    "particle": "${mappedSingleTexture(data.texture, "blocks", modid)}"
    </#if>
  }
}