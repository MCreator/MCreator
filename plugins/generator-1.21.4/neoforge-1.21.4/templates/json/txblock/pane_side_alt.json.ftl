{
    "parent": "block/template_glass_pane_side_alt",
    "textures": {
        <#if data.particleTexture?has_content>"particle": "${data.particleTexture.format("%s:block/%s")}",</#if>
        "edge": "${data.textureTop().format("%s:block/%s")}",
        "pane": "${data.texture.format("%s:block/%s")}"
    },
    "render_type": "${data.getRenderType()}"
}