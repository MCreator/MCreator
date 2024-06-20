{
    "parent": "block/template_glass_pane_noside",
    "textures": {
        <#if data.particleTexture?? && !data.particleTexture.isEmpty()>"particle": "${modid}:block/${data.particleTexture}",</#if>
        "pane": "${modid}:block/${data.texture}"
    },
    "render_type": "${data.getRenderType()}"
}