<#-- @formatter:off -->
<#include "../mcitems.ftl">
<#assign ct = data.treeType == data.TREES_CUSTOM>
{
  "type": "minecraft:tree",
  "config": {
    "force_dirt": false,
	"ignore_vines": true,
	<#if data.hasVines() || data.hasFruits()>
	  "decorators": [
	    <#if data.hasVines()>
	    {
	      "type": "${modid}:${registryname}tree_trunk_decorator"
	    },
	    {
	      "type": "${modid}:${registryname}_tree_leave_decorator"
	    }<#if data.hasFruits()>,</#if>
        </#if>
	    <#if data.hasFruits()>
	    {
	      "type": "${modid}:${registryname}_tree_fruit_decorator"
	    }
        </#if>
	  ],
	</#if>
	<#if data.vanillaTreeType == "Big trees">

	<#else>
	  "minimum_size": {
	    "type": "minecraft:two_layers_feature_size",
	    "limit": 1,
	    "lower_size": 0,
	    "upper_size": 1
	  },
	  "dirt_provider": {
	    "type": "minecraft:simple_state_provider",
	    "state": ${mappedMCItemToBlockStateJSON(data.undergroundBlock)}
	  },
	  "trunk_provider": {
	    "type": "minecraft:simple_state_provider",
	    "state":
	    <#if ct>
	    ${mappedMCItemToBlockStateJSON(data.treeStem)}
	    <#else>
	    {
	      "Name": "minecraft:oak_log",
	      "Properties": {
	        "axis": "y"
	      }
	    }
	    </#if>
	  },
	  "foliage_provider": {
	    "type": "minecraft:simple_state_provider",
	    "state":
	    <#if ct>
	    ${mappedMCItemToBlockStateJSON(data.treeBranch)}
	    <#else>
	    {
	      "Name": "minecraft:oak_leaves",
	      "Properties": {
	        "distance": "7",
	        "persistent": "false",
	        "waterlogged": "false"
	      }
	    }
	    </#if>
	  },
	  "trunk_placer": {
	    "type": "minecraft:straight_trunk_placer",
	    "base_height": ${ct?then([data.minHeight, 32]?min, 4)},
	    "height_rand_a": 2,
	    "height_rand_b": 0
	  },
	  "foliage_placer": {
	    "type": "minecraft:blob_foliage_placer",
	    "radius": 2,
	    "offset": 0,
	    "height": 3
	  }
	</#if>
  }
}
<#-- @formatter:on -->