<#include "../textures.ftl">
{
    "parent": "block/template_fence_gate_wall",
    "textures": {
      <#if data.particleTexture?has_content>
      "particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}",
      </#if>
      "texture": "${mappedSingleTexture(data.texture, "blocks", modid)}"
    }
}