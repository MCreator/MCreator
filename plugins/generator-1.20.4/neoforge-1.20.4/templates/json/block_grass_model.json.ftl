{
  "parent": "block/grass_block",
  "textures": {
    "bottom": "${modid}:block/${data.texture}",
    "top": "${modid}:block/${data.textureTop?has_content?then(data.textureTop, data.texture)}",
    "side": "${modid}:block/${data.textureFront?has_content?then(data.textureFront, data.texture)}",
    "overlay": "${modid}:block/${data.textureLeft?has_content?then(data.textureLeft, data.texture)}",
    "particle": "${modid}:block/${data.particleTexture?has_content?then(data.particleTexture, data.texture)}"
  },
  "render_type": "${data.getRenderType()}"
}