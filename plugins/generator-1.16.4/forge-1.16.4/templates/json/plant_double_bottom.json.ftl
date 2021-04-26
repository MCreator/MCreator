{
    "parent": "block/${var_model}",
    "textures": {
        "cross": "${modid}:blocks/${data.textureBottom?has_content?then(data.textureBottom, data.texture)}",
        "particle": "${modid}:blocks/<#if data.particleTexture?has_content>${data.particleTexture}<#else>${data.textureBottom?has_content?then(data.textureBottom, data.texture)}</#if>"
    }
}