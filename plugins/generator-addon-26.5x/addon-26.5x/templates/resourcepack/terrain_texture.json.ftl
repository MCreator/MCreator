{
  "resource_pack_name": "vanilla",
  "texture_name": "atlas.terrain",
  "padding": 8,
  "num_mip_levels": 0,
  "texture_data": {
  <#list beblocks as block>
  	<#assign registryname = block.getModElement().getRegistryName()>
  	<#if block.hasOneTexture()>
  	"${modid}_${registryname}": { "textures": [ "textures/blocks/${block.texture}" ] }
  	<#else>
  	"${modid}_${registryname}_up": { "textures": [ "textures/blocks/${block.textureTop()}" ] },
  	"${modid}_${registryname}_down": { "textures": [ "textures/blocks/${block.texture}" ] },
  	"${modid}_${registryname}_south": { "textures": [ "textures/blocks/${block.textureFront()}" ] },
  	"${modid}_${registryname}_north": { "textures": [ "textures/blocks/${block.textureBack()}" ] },
  	"${modid}_${registryname}_west": { "textures": [ "textures/blocks/${block.textureLeft()}" ] },
  	"${modid}_${registryname}_east": { "textures": [ "textures/blocks/${block.textureRight()}" ] }
  	</#if><#sep>,
  	</#list>
  }
}