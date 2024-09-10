{
  "parent": "block/${var_model}",
  "textures": {
    "cross": "${data.textureBottom().format("%s:block/%s")}",
    <#if data.particleTexture?has_content>
    "particle": "${data.particleTexture.format("%s:block/%s")}"
    <#else>
    "particle": "${data.textureBottom().format("%s:block/%s")}"
    </#if>
  },
  "render_type": "cutout"
}