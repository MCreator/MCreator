{
  "parent": "block/template_glass_pane_side",
  "textures": {
    <#if data.particleTexture?has_content>"particle": "${modid}:blocks/${data.particleTexture}",</#if>
    "edge": "${modid}:blocks/${data.textureTop?has_content?then(data.textureTop, data.texture)}",
    "pane": "${modid}:blocks/${data.texture}"
  }
}