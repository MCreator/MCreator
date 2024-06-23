{
    "parent": "block/template_glass_pane_noside",
    "textures": {
        <#if data.particleTexture?? && !data.particleTexture.isEmpty()>"particle": "${data.particleTexture.format("%s:block/%s")}",</#if>
        "pane": "${data.texture.format("%s:block/%s")}"
    },
    "render_type": "${data.getRenderType()}"
}