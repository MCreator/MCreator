<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
	"format_version": "1.17.0",
	"minecraft:ore_feature": {
		"description": {
			"identifier": "${modid}:${modid}_${registryname}_ore_feature"
		},
		"count": ${data.frequencyOnChunk},
		"replace_rules": [
			{
				"places_block": {
					"name": "${modid}:${registryname}",
					"states": {}
				},
			     
				"may_replace": [
					{
                           "name":<#list data.blocksToReplace as block>
                           "${mappedMCItemToIngameNameNoTags(block)}"<#if block?has_next>,</#if>
                            </#list>
					}
				]
			}
		]
	}
}
<#-- @formatter:on -->
