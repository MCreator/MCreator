{
    "parent": "block/template_glass_pane_noside",
    "textures": {
        <#if data.particleTexture?has_content>"particle": "${data.particleTexture.format("%s:block/%s")}",</#if>
        "pane": "${data.texture.format("%s:block/%s")}"
    },
    "render_type": "${data.getRenderType()}"
}