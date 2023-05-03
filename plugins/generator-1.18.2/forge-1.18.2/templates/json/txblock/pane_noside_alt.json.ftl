{
  "parent": "block/template_glass_pane_noside_alt",
  "textures": {
    <#if data.particleTexture?has_content>"particle": "${modid}:blocks/${data.particleTexture}",</#if>
    "pane": "${modid}:blocks/${data.texture}"
  }
}