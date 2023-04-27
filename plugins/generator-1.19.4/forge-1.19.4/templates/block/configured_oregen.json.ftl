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
                    "predicate_type": "blockstate_match",
                    "block_state": ${mappedMCItemToBlockStateJSON(replacementBlock)}
                },
                "state": {
                    "Name": "${modid}:${registryname}"
                }
            }<#sep>,
            </#list>
        ]
    }
}