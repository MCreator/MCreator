{
    "parent": "block/door_top_rh",
    "textures": {
      "bottom": "${modid}:blocks/${data.texture}",
      "top": "${modid}:blocks/${data.textureTop?has_content?then(data.textureTop, data.texture)}"
    }
}
