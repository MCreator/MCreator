{
  "parent": "block/${var_model}",
  "textures": {
    "cross": "${modid}:block/${data.textureBottom()}",
    "particle": "${modid}:block/<#if data.particleTexture?? && !data.particleTexture.isEmpty()>${data.particleTexture}<#else>${data.textureBottom()}</#if>"
  },
  "render_type": "cutout"
}