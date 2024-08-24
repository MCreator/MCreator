<#if data.textureBucket?has_content>
{
  "parent": "item/generated",
  "textures": {
    "layer0": "${data.textureBucket.format("%s:item/%s")}"
  }
}
<#else>
{
  "parent": "neoforge:item/bucket_drip",
  "loader": "neoforge:fluid_container",
  "fluid": "${modid}:${registryname}"
}
</#if>