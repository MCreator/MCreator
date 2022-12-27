{
    "parent": "item/generated",
    "textures": {
        <#if var_item??>
            "layer0": "${modid}:items/${data.getArmorPartTexture(var_item)}"
        <#else>
            "layer0": "${modid}:items/${data.texture}"
        </#if>
    }
}