<#include "../textures.ftl">
{
  "parent": "block/template_glass_pane_noside_alt",
  "textures": {
    <#if data.particleTexture?has_content>
    "particle": "${mappedSingleTexture(data.particleTexture, "blocks", modid)}"
    </#if>
    "pane": "${mappedSingleTexture(data.texture, "blocks", modid)}"
  }
}