{
    "parent": "block/door_bottom_right_open",
    "textures": {
      <#if data.particleTexture?has_content>"particle": "${modid}:blocks/${data.particleTexture}",</#if>
      "bottom": "${modid}:blocks/${data.texture}",
      "top": "${modid}:blocks/${data.textureTop?has_content?then(data.textureTop, data.texture)}"
    }
}
