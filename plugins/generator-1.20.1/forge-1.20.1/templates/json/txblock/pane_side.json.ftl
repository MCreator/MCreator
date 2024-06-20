{
    "parent": "block/template_glass_pane_side",
    "textures": {
        <#if data.particleTexture?? && !data.particleTexture.isEmpty()>"particle": "${modid}:block/${data.particleTexture}",</#if>
        "edge": "${modid}:block/${data.textureTop()}",
        "pane": "${modid}:block/${data.texture}"
    },
    "render_type": "${data.getRenderType()}"
}