<#include "../textures.ftl">
{
  "parent": "block/cube",
  "textures": {
    "down": "${mappedSingleTexture(data.texture, "blocks", modid)}",
    <#if data.textureTop?has_content>
    "up": "${mappedSingleTexture(data.textureTop, "blocks", modid)}",
    <#else>
    "up": "${mappedSingleTexture(data.texture, "blocks", modid)}",
    </#if>
    <#if data.textureFront?has_content>
    "north": "${mappedSingleTexture(data.textureFront, "blocks", modid)}",
    <#else>
    "north": "${mappedSingleTexture(data.texture, "blocks", modid)}",
    </#if>
    <#if data.textureLeft?has_content>
    "east": "${mappedSingleTexture(data.textureLeft, "blocks", modid)}",
    <#else>
    "east": "${mappedSingleTexture(data.texture, "blocks", modid)}",
    </#if>
    <#if data.textureBack?has_content>
    "south": "${mappedSingleTexture(data.textureBack, "blocks", modid)}",
    <#else>
    "south": "${mappedSingleTexture(data.texture, "blocks", modid)}",
    </#if>
    <#if data.textureRight?has_content>
    "west": "${mappedSingleTexture(data.textureRight, "blocks", modid)}",
    <#else>
    "west": "${mappedSingleTexture(data.texture, "blocks", modid)}",
    </#if>
    <#if data.particleTexture?has_content>
    "particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}"
    <#else>
    "particle": "${mappedSingleTexture(data.texture, "blocks", modid)}"
    </#if>
  }
}