{
<#if data.hasCustomJAVAModel?? && data.hasCustomJAVAModel()>
	<#assign parent = "builtin/entity">
	<#assign texture = "particle">
	"gui_light": "front",
<#else>
	<#assign parent = "item/generated">
	<#assign texture = "layer0">
</#if>
    "parent": "${parent}",
    "textures": {
        <#if var_item??>
            "${texture}": "${data.getItemTextureFor(var_item).format("%s:item/%s")}"
        <#else>
            "${texture}": "${data.texture.format("%s:item/%s")}"
        </#if>
    }
}