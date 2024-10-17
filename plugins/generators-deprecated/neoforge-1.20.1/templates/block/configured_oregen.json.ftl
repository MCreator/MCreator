<#include "../mcitems.ftl">
{
    "type": "${modid}:${registryname}",
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
                	<#elseif generator.map(replacementBlock.getUnmappedValue(), "blocksitems", 1).startsWith("#")>
						"predicate_type": "tag_match",
						"tag": "${generator.map(replacementBlock.getUnmappedValue(), "blocksitems", 1).replace("#", "")}"
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