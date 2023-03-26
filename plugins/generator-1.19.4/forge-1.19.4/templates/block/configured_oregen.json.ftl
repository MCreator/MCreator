<#include "../mcitems.ftl">
{
    "type": "minecraft:ore",
    "config": {
		"size": ${data.frequencyOnChunk},
		"discard_chance_on_air_exposure": 0,
		"targets": [
			<#list data.blocksToReplace as replacementBlock>
			{
				"target": {
					"predicate_type": "blockstate_match",
					"block_state": ${mappedBlockToBlockStateProvider(replacementBlock)}
				},
				"state": "${modid}:${data.getModElement().getRegistryName()}"
			}<#sep>,
			</#list>
		]
	}
}