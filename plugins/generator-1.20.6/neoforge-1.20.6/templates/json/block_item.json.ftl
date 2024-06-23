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
    "parent": "${modid}:block/${registryname}",
    "display": {
      "thirdperson": {
        "rotation": [
          10,
          -45,
          170
        ],
        "translation": [
          0,
          1.5,
          -2.75
        ],
        "scale": [
          0.375,
          0.375,
          0.375
        ]
      }
    }
}
</#if>
<#-- @formatter:on -->