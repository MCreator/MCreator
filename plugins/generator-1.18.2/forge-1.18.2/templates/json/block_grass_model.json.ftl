{
  "parent": "block/grass_block",
  "textures": {
    "bottom": "${modid}:blocks/${data.texture}",
    "top": "${modid}:blocks/${data.textureTop?has_content?then(data.textureTop, data.texture)}",
    "side": "${modid}:blocks/${data.textureFront?has_content?then(data.textureFront, data.texture)}",
    "overlay": "${modid}:blocks/${data.textureLeft?has_content?then(data.textureLeft, data.texture)}",
    "particle": "${modid}:blocks/${data.particleTexture?has_content?then(data.particleTexture, data.texture)}"
  }
}