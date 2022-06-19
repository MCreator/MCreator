<#include "../textures.ftl">
<#if data.textureBucket?has_content>
{
  "parent": "item/generated",
  "textures": {
    "layer0": "${mappedSingleTexture(data.textureBucket, "items", modid)}"
  }
}
<#else>
{
  "parent": "forge:item/bucket_drip",
  "loader": "forge:bucket",
  "fluid": "${modid}:${registryname}"
}
</#if>