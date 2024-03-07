<#-- @formatter:off -->
<#include "../mcitems.ftl">

<#assign ct = data.treeType == data.TREES_CUSTOM>

<#if data.vanillaTreeType == "Big trees">
    <#assign minimum_size = [1, 1, 2]>
    <#assign foliage_placer = ["minecraft:jungle_foliage_placer", 2, 0, 2]>
    <#assign trunk_placer = ["minecraft:mega_jungle_trunk_placer", ct?then([data.minHeight, 32]?min, 10), 2, 19]>
    <#assign default_log = "minecraft:jungle_log">
    <#assign default_leaves = "minecraft:jungle_leaves">
<#elseif data.vanillaTreeType == "Savanna trees">
	<#assign minimum_size = [1, 0, 2]>
	<#assign foliage_placer = ["minecraft:acacia_foliage_placer", 2, 0]>
    <#assign trunk_placer = ["minecraft:forking_trunk_placer", ct?then([data.minHeight, 32]?min, 5), 2, 2]>
    <#assign default_log = "minecraft:acacia_log">
    <#assign default_leaves = "minecraft:acacia_leaves">
<#elseif data.vanillaTreeType == "Mega pine trees">
    <#assign minimum_size = [1, 1, 2]>
    <#assign foliage_placer = ["minecraft:mega_pine_foliage_placer", 0, 0, 3, 7]>
    <#assign trunk_placer = ["minecraft:giant_trunk_placer", ct?then([data.minHeight, 32]?min, 13), 2, 14]>
    <#assign default_log = "minecraft:spruce_log">
    <#assign default_leaves = "minecraft:spruce_leaves">
<#elseif data.vanillaTreeType == "Mega spruce trees">
    <#assign minimum_size = [1, 1, 2]>
    <#assign foliage_placer = ["minecraft:mega_pine_foliage_placer", 0, 0, 13, 17]>
    <#assign trunk_placer = ["minecraft:giant_trunk_placer", ct?then([data.minHeight, 32]?min, 13), 2, 14]>
    <#assign default_log = "minecraft:spruce_log">
    <#assign default_leaves = "minecraft:spruce_leaves">
<#elseif data.vanillaTreeType == "Birch trees">
    <#assign minimum_size = [1, 0, 1]>
    <#assign foliage_placer = ["minecraft:blob_foliage_placer", 2, 0, 3]>
    <#assign trunk_placer = ["minecraft:straight_trunk_placer", ct?then([data.minHeight, 32]?min, 5), 2, 0]>
    <#assign default_log = "minecraft:birch_log">
    <#assign default_leaves = "minecraft:birch_leaves">
<#else>
    <#assign minimum_size = [1, 0, 1]>
    <#assign foliage_placer = ["minecraft:blob_foliage_placer", 2, 0, 3]>
    <#assign trunk_placer = ["minecraft:straight_trunk_placer", ct?then([data.minHeight, 32]?min, 4), 2, 0]>
    <#assign default_log = "minecraft:oak_log">
    <#assign default_leaves = "minecraft:oak_leaves">
</#if>

{
  "type": "minecraft:tree",
  "config": {
    "force_dirt": false,
    "ignore_vines": true,
    "minimum_size": {
      "type": "minecraft:two_layers_feature_size",
      "limit": ${minimum_size[0]},
      "lower_size": ${minimum_size[1]},
      "upper_size": ${minimum_size[2]}
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
          "Name": "${default_log}",
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
          "Name": "${default_leaves}",
          "Properties": {
            "distance": "7",
            "persistent": "false",
            "waterlogged": "false"
           }
        }
      </#if>
    },
    "trunk_placer": {
      "type": "${trunk_placer[0]}",
      "base_height": ${trunk_placer[1]},
      "height_rand_a": ${trunk_placer[2]},
      "height_rand_b": ${trunk_placer[3]}
    },
    "foliage_placer": {
      "type": "${foliage_placer[0]}",
      "radius": ${foliage_placer[1]},
      "offset": ${foliage_placer[2]}
      <#if foliage_placer?size == 4>,
	    "height": ${foliage_placer[3]}
      <#elseif foliage_placer?size == 5>,
        "crown_height": {
          "type": "minecraft:uniform",
          "value": {
            "min_inclusive": ${foliage_placer[3]},
            "max_inclusive": ${foliage_placer[4]}
          }
        }
	  </#if>
    },
    "decorators": [
    <#if data.hasVines() || data.hasFruits()>
      <#if data.hasFruits()>
        {
          "type": "${modid}:${registryname}_tree_fruit_decorator"
        }<#if data.hasVines()>,</#if>
      </#if>
      <#if data.hasVines()>
        {
          "type": "${modid}:${registryname}_tree_trunk_decorator"
        },
        {
          "type": "${modid}:${registryname}_tree_leave_decorator"
        }
	  </#if>
    </#if>
    ]
  }
}
<#-- @formatter:on -->