<#-- @formatter:off -->
<#if data.itemTexture?has_content>
{
  "parent": "item/generated",
  "textures": {
    "layer0": "${data.itemTexture.format("%s:item/%s")}"
  },
  "render_type": "translucent"
}
<#else>
{
  "parent": "${modid}:block/${registryname}"
}
</#if>
<#-- @formatter:on -->