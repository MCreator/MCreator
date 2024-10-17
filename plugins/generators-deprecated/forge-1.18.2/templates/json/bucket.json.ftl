<#if data.textureBucket?has_content>
{
  "parent": "item/generated",
  "textures": {
    "layer0": "${modid}:items/${data.textureBucket}"
  }
}
<#else>
{
  "parent": "forge:item/bucket_drip",
  "loader": "forge:bucket",
  "fluid": "${modid}:${registryname}"
}
</#if>