<#if data.textureBucket?has_content>
{
  "parent": "item/generated",
  "textures": {
    "layer0": "${data.textureBucket.format("%s:item/%s")}"
  }
}
<#else>
{
  "parent": "forge:item/bucket_drip",
  "loader": "forge:fluid_container",
  "fluid": "${modid}:${registryname}"
}
</#if>