{
    "parent": "block/${var_model}",
    "textures": {
      "bottom": "${modid}:blocks/${data.texture}",
      "top": "${modid}:blocks/${data.textureTop?has_content?then(data.textureTop, data.texture)}",
      "side": "${modid}:blocks/${data.textureFront?has_content?then(data.textureFront, data.texture)}"
    }
}