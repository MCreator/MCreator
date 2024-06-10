<#if data.textureBucket?has_content>
{
  "parent": "item/generated",
  "textures": {
    "layer0": "${modid}:item/${data.textureBucket}"
  }
}
<#else>
{
  "parent": "neoforge:item/bucket_drip",
  "loader": "neoforge:fluid_container",
  "fluid": "${modid}:${registryname}"
}
</#if>