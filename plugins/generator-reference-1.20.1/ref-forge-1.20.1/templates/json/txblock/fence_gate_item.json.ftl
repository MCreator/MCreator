<#-- @formatter:off -->
<#if data.itemTexture?has_content>
{
  "parent": "item/generated",
  "textures": {
    "layer0": "${modid}:item/${data.itemTexture}"
  },
  "render_type": "translucent"
}
<#else>
{
  "parent": "${modid}:block/${registryname}"
}
</#if>
<#-- @formatter:on -->