<#-- @formatter:off -->
<#include "../textures.ftl">
<#if data.itemTexture?has_content>
{
  "parent": "item/generated",
  "textures": {
    "layer0": "${mappedSingleTexture(data.itemTexture, "items", modid)}"
  }
}
<#else>
{
    "parent": "item/generated",
    "textures": {
      "layer0": "${mappedSingleTexture(data.texture, "blocks", modid)}"
    }
}
</#if>
<#-- @formatter:on -->