{
  "parent": "block/cube",
  "textures": {
  "down": "${modid}:blocks/${data.texture}",
  "up": "${modid}:blocks/${data.textureTop?has_content?then(data.textureTop, data.texture)}",
  "north": "${modid}:blocks/${data.textureFront?has_content?then(data.textureFront, data.texture)}",
  "east": "${modid}:blocks/${data.textureRight?has_content?then(data.textureLeft, data.texture)}",
  "south": "${modid}:blocks/${data.textureLeft?has_content?then(data.textureBack, data.texture)}",
  "west": "${modid}:blocks/${data.textureBack?has_content?then(data.textureRight, data.texture)}",
  "particle": "${modid}:blocks/${data.texture}"
  }
}