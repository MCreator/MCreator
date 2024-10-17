{
  "parent": "block/${var_model}",
  "textures": {
    "${var_txname}": "${data.texture.format("%s:block/%s")}",
    "particle": "${data.getParticleTexture().format("%s:block/%s")}"
  }
  <#if !(data.blockBase?has_content && data.blockBase == "Leaves")>
  ,"render_type": "${data.getRenderType()}"
  </#if>
}