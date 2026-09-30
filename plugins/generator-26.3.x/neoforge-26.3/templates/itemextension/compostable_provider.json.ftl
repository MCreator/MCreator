<#if data.compostLayerChance == 1>
1
<#else>
{
	"type": "minecraft:number_dispatcher",
	"cases": [
		{
			"condition": {
				"type": "minecraft:match_block",
				"blocks": "minecraft:composter",
				"state": {
					"level": "0"
				}
			},
			"value": 1
		}
	],
	"default": {
		"type": "minecraft:weighted_list",
		"distribution": [
			{
				"data": 1,
				"weight": ${(data.compostLayerChance*10000)?round}
			},
			{
				"data": 0,
				"weight": ${((1 - data.compostLayerChance)*10000)?round}
			}
		]
	}
}
</#if>