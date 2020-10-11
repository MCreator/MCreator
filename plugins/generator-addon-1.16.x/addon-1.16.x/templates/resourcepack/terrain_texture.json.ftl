{
	"resource_pack_name": "vanilla",
	"texture_name": "atlas.terrain",
	"padding": 8,
	"num_mip_levels": 0,
	"texture_data": {
		<#list w.getElementsOfType("BLOCK") as mod>
			<#assign ge = mod.getGeneratableElement()>
    		"${modid}_${mod.getRegistryName()}_up": { "textures": [ "textures/blocks/${ge.textureTop}" ] },
    		"${modid}_${mod.getRegistryName()}_down": { "textures": [ "textures/blocks/${ge.texture}" ] },
    		"${modid}_${mod.getRegistryName()}_south": { "textures": [ "textures/blocks/${ge.textureFront}" ] },
    		"${modid}_${mod.getRegistryName()}_north": { "textures": [ "textures/blocks/${ge.textureBack}" ] },
    		"${modid}_${mod.getRegistryName()}_west": { "textures": [ "textures/blocks/${ge.textureLeft}" ] },
    		"${modid}_${mod.getRegistryName()}_east": { "textures": [ "textures/blocks/${ge.textureRight}" ] }<#if mod?has_next>,</#if>
		</#list>
    }
}