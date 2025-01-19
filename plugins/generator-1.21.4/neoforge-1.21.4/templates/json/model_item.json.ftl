{
  "model": {
    "type": "minecraft:model",
	<#if var_sufix??>
	"model": "${modid}:item/${registryname}${var_sufix}"
	<#else>
	"model": "${modid}:item/${registryname}"
	</#if>
  }
}