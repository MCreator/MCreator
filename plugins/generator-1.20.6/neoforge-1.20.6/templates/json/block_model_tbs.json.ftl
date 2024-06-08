{
    "parent": "block/${var_model}",
    "textures": {
      <#if data.particleTexture?has_content>"particle": "${modid}:block/${data.particleTexture}",</#if>
      "bottom": "${modid}:block/${data.texture}",
      "top": "${modid}:block/${data.textureTop?has_content?then(data.textureTop, data.texture)}",
      "side": "${modid}:block/${data.textureFront?has_content?then(data.textureFront, data.texture)}"
    },
    "render_type": "${data.getRenderType()}"
}