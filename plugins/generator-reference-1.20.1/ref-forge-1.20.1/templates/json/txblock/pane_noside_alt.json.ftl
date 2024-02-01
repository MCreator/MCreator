{
    "parent": "block/template_glass_pane_noside_alt",
    "textures": {
        <#if data.particleTexture?has_content>"particle": "${modid}:block/${data.particleTexture}",</#if>
        "pane": "${modid}:block/${data.texture}"
    },
    "render_type": "${data.getRenderType()}"
}