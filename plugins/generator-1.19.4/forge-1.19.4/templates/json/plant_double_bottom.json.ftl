{
  "parent": "block/${var_model}",
  "textures": {
    "cross": "${modid}:block/${data.textureBottom?has_content?then(data.textureBottom, data.texture)}",
    "particle": "${modid}:block/<#if data.particleTexture?has_content>${data.particleTexture}<#else>${data.textureBottom?has_content?then(data.textureBottom, data.texture)}</#if>"
  },
  "render_type": "cutout"
}