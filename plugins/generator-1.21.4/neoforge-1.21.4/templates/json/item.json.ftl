<#if !data.hasJavaModel()>
	<#assign parent = "item/generated">
	<#assign texture = "layer0">
<#else>
	<#assign parent = "builtin/entity">
	<#assign texture = "particle">
</#if>
{
    "parent": "${parent}",
    "textures": {
        <#if var_item??>
            "${texture}": "${data.getItemTextureFor(var_item).format("%s:item/%s")}"
        <#else>
            "${texture}": "${data.texture.format("%s:item/%s")}"
        </#if>
    }
}