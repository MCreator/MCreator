<#-- @formatter:off -->
<#if data.itemTexture?? && !data.itemTexture.isEmpty()>
{
  "parent": "item/generated",
  "textures": {
    "layer0": "${data.itemTexture.format("%s:item/%s")}"
  },
  "render_type": "translucent"
}
<#else>
{
  "parent": "${modid}:block/${registryname}_inventory"
}
</#if>
<#-- @formatter:on -->