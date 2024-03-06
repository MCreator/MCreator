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
                	<#if replacementBlock.getUnmappedValue().startsWith("TAG:")>
						"predicate_type": "tag_match",
						"tag": "${replacementBlock.getUnmappedValue().replace("TAG:", "")}"
                	<#elseif replacementBlock.getMappedValue(1).startsWith("#")>
						"predicate_type": "tag_match",
						"tag": "${replacementBlock.getMappedValue(1).replace("#", "")}"
                	<#else>
						"predicate_type": "blockstate_match",
						"block_state": ${mappedMCItemToBlockStateJSON(replacementBlock)}
                	</#if>
                },
                "state": {
                    "Name": "${modid}:${registryname}"
                }
            }<#sep>,
            </#list>
        ]
    }
}