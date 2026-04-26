{
  "model": {
	<#if data.hasCustomBucketTexture()>
    "type": "minecraft:model",
	"model": "${modid}:item/${registryname}_bucket"
	<#else>
	"type": "neoforge:fluid_container",
    "fluid": "${modid}:${registryname}",
    "textures": {
      "base": "item/bucket",
      "fluid": "neoforge:item/mask/bucket_fluid",
      "cover": "neoforge:item/mask/bucket_fluid_cover"
    }
	</#if>
  }
}