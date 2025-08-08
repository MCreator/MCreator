{
    "parent": "block/${var_model}",
    "textures": {
      <#if data.particleTexture?has_content>"particle": "${data.particleTexture.format("%s:block/%s")}",</#if>
      "bottom": "${data.texture.format("%s:block/%s")}",
      "top": "${data.textureTop().format("%s:block/%s")}",
      "side": "${data.textureFront().format("%s:block/%s")}"
    },
    "render_type": "${data.getRenderType()}"
}