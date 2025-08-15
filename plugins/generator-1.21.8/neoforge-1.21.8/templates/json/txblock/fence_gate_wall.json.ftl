{
    "parent": "block/template_fence_gate_wall",
    "textures": {
        <#if data.particleTexture?has_content>"particle": "${data.particleTexture.format("%s:block/%s")}",</#if>
        "texture": "${data.texture.format("%s:block/%s")}"
    },
    "render_type": "${data.getRenderType()}"
}