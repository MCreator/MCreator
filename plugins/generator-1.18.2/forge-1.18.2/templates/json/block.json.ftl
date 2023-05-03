{
    "parent": "block/${var_model}",
    "textures": {
        "${var_txname}": "${modid}:blocks/${data.texture}",
        "particle": "${modid}:blocks/${data.particleTexture?has_content?then(data.particleTexture, data.texture)}"
    }
}