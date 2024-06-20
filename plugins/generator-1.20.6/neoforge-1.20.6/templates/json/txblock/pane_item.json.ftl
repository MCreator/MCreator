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
  "parent": "item/generated",
  "textures": {
    "layer0": "${modid}:block/${data.texture}"
  },
  "render_type": "${data.getRenderType()}"
}
</#if>
<#-- @formatter:on -->