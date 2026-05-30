{
  "model": {
    "type": "minecraft:model",
	"model": "${modid}:item/${registryname}"
	<#if data.tintType != "No tint" && data.isItemTinted>,
	"tints": [
      {
	    <#if data.tintType == "Grass">
		"type": "minecraft:grass",
        "downfall": 1.0,
        "temperature": 0.5
	    <#elseif data.tintType == "Foliage" || data.tintType == "Default foliage">
		"type": "minecraft:constant",
		"value": -12012264
	    <#elseif data.tintType == "Birch foliage">
		"type": "minecraft:constant",
		"value": -8345771
	    <#elseif data.tintType == "Spruce foliage">
		"type": "minecraft:constant",
		"value": -10380959
	    <#elseif data.tintType == "Water">
		"type": "minecraft:constant",
		"value": 3694022
	    <#elseif data.tintType == "Sky">
		"type": "minecraft:constant",
		"value": 8562943
	    <#elseif data.tintType == "Fog">
		"type": "minecraft:constant",
		"value": 12638463
	    <#else>
		"type": "minecraft:constant",
		"value": 329011
	    </#if>
      }
    ]
	</#if>
  }
}