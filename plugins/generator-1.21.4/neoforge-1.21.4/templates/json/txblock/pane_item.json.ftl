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
  "parent": "item/generated",
  "textures": {
    "layer0": "${data.texture.format("%s:block/%s")}"
  },
  "render_type": "${data.getRenderType()}"
}
</#if>
<#-- @formatter:on -->