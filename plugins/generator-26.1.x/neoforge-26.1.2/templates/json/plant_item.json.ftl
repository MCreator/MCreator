{
  "parent": "item/generated",
  "textures": {
    <#if data.itemTexture?has_content>
    "layer0": "${data.itemTexture.format("%s:item/%s")}"
    <#else>
    "layer0": "${data.texture.format("%s:block/%s")}"
    </#if>
  }
}