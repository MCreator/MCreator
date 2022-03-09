<#-- @formatter:off -->
<#if data.itemTexture?has_content>
{
  "parent": "item/generated",
  "textures": {
    "layer0": "${modid}:items/${data.itemTexture}"
  }
}
<#else>
{
    "parent": "item/generated",
    "textures": {
      "layer0": "${modid}:blocks/${data.texture}"
    }
}
</#if>
<#-- @formatter:on -->