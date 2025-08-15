<#if data.textureBucket?has_content>
{
  "parent": "item/generated",
  "textures": {
    "layer0": "${data.textureBucket.format("%s:item/%s")}"
  }
}
<#else>
{
  "parent": "neoforge:items/fluid_container",
  "fluid": "${modid}:${registryname}"
}
</#if>