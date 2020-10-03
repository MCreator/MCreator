{
    "parent": "item/generated",
    "textures": {
        "layer0":
        <#if data.itemTexture?has_content>
		"${modid}:items/${data.itemTexture}"
        <#else>
        "${modid}:blocks/${data.texture}"
        </#if>
    }
}