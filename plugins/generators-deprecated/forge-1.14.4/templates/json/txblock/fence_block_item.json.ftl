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
  "parent": "${modid}:block/${registryname}_inventory"
}
</#if>
<#-- @formatter:on -->