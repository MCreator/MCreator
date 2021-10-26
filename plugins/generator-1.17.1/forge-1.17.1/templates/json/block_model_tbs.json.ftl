{
    "parent": "block/${var_model}",
    "textures": {
      <#if data.particleTexture?has_content>"particle": "${modid}:blocks/${data.particleTexture}",</#if>
      "bottom": "${modid}:blocks/${data.texture}",
      "top": "${modid}:blocks/${data.textureTop?has_content?then(data.textureTop, data.texture)}",
      "side": "${modid}:blocks/${data.textureFront?has_content?then(data.textureFront, data.texture)}"
    }
}