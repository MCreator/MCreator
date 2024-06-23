{
  "parent": "block/${var_model}",
  "textures": {
    "cross": "${data.textureBottom().format("%s:block/%s")}",
    "particle": "${modid}:block/<#if data.particleTexture?? && !data.particleTexture.isEmpty()>${data.particleTexture}<#else>${data.textureBottom()}</#if>"
  },
  "render_type": "cutout"
}