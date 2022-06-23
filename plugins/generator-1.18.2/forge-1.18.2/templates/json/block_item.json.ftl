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
    "parent": "${modid}:block/${registryname}",
    "display": {
      "thirdperson": {
        "rotation": [
          10,
          -45,
          170
        ],
        "translation": [
          0,
          1.5,
          -2.75
        ],
        "scale": [
          0.375,
          0.375,
          0.375
        ]
      }
    }
}
</#if>
<#-- @formatter:on -->