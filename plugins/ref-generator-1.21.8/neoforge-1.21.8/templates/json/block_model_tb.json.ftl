{
    "parent": "block/${var_model}",
    "textures": {
      <#if data.particleTexture?has_content>"particle": "${data.particleTexture.format("%s:block/%s")}",</#if>
      "${var_txname}": "${data.texture.format("%s:block/%s")}",
      "${var_txname_top}": "${data.textureTop().format("%s:block/%s")}"
    },
    "render_type": "${data.getRenderType()}"
}