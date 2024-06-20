{
    "parent": "block/${var_model}",
    "textures": {
      <#if data.particleTexture?? && !data.particleTexture.isEmpty()>"particle": "${modid}:block/${data.particleTexture}",</#if>
      "bottom": "${modid}:block/${data.texture}",
      "top": "${modid}:block/${data.textureTop()}",
      "side": "${modid}:block/${data.textureFront()}"
    },
    "render_type": "${data.getRenderType()}"
}