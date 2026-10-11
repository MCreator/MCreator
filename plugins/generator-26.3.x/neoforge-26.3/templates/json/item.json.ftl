{
    <#assign hasJavaModel = data.hasCustomJAVAModel?? && data.hasCustomJAVAModel()>
    <#if hasJavaModel>
    "gui_light": "front",
    </#if>
    "parent": "<#if hasJavaModel>builtin/entity<#else>item/generated</#if>",
    "textures": {
        <#if var_item??>
            "layer0": "${data.getItemTextureFor(var_item).format("%s:item/%s")}"
        <#else>
            "layer0": "${data.texture.format("%s:item/%s")}"
        </#if>
    }
}