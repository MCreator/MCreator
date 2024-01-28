{
  "parent": "block/cube",
  "textures": {
    "down": "${modid}:block/${data.texture}",
    "up": "${modid}:block/${data.textureTop?has_content?then(data.textureTop, data.texture)}",
    "north": "${modid}:block/${data.textureFront?has_content?then(data.textureFront, data.texture)}",
    "east": "${modid}:block/${data.textureLeft?has_content?then(data.textureLeft, data.texture)}",
    "south": "${modid}:block/${data.textureBack?has_content?then(data.textureBack, data.texture)}",
    "west": "${modid}:block/${data.textureRight?has_content?then(data.textureRight, data.texture)}",
    "particle": "${modid}:block/${data.particleTexture?has_content?then(data.particleTexture, data.texture)}"
  },
  "render_type": "${data.getRenderType()}"
}