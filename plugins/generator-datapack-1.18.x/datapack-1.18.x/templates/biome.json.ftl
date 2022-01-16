<#-- @formatter:off -->
<#-- now in dimension: scale, depth, surface and underground block -->
{
    "precipitation": <#if (data.rainingPossibility > 0)><#if (data.temperature > 0.15)>"rain"<#else>"snow"</#if><#else>"none"</#if>,
    "temperature": ${data.temperature},
    "downfall": ${data.rainingPossibility},
    "category": "${data.biomeCategory?replace("THEEND", "THE_END")?lower_case}",
    "effects": {
    	"foliage_color": ${data.foliageColor?has_content?then(data.foliageColor.getRGB(), 10387789)},
    	"grass_color": ${data.grassColor?has_content?then(data.grassColor.getRGB(), 9470285)},
    	"sky_color": ${data.airColor?has_content?then(data.airColor.getRGB(), 7972607)},
    	"fog_color": ${data.airColor?has_content?then(data.airColor.getRGB(), 12638463)},
    	"water_color": ${data.waterColor?has_content?then(data.waterColor.getRGB(), 4159204)},
    	"water_fog_color": ${data.waterFogColor?has_content?then(data.waterFogColor.getRGB(), 329011)}
    },
	"spawners": {
		"monster": [<@generateEntityList data.spawnEntries "monster"/>],
		"creature": [<@generateEntityList data.spawnEntries "creature"/>],
		"ambient": [<@generateEntityList data.spawnEntries "ambient"/>],
		"water_creature": [<@generateEntityList data.spawnEntries "waterCreature"/>],
		"water_ambient": [],
		"misc": []
	},
	"spawn_costs": {},
    "carvers": {
		<#if data.defaultFeatures?contains("Caves")>
    	"air": [
            "minecraft:cave",
            "minecraft:canyon"
    	]
		</#if>
    },
    "features": [
    	<#--RAW_GENERATION-->[],
		<#--LAKES-->[
		<#if data.defaultFeatures?contains("Lakes")>
			"minecraft:lake_water",
			"minecraft:lake_lava"
		</#if>
    	],
		<#--LOCAL_MODIFICATIONS-->[],
		<#--UNDERGROUND_STRUCTURES-->[
		<#if data.defaultFeatures?contains("MonsterRooms")>
			"minecraft:monster_room"
		</#if>
    	],
		<#--SURFACE_STRUCTURES-->[],
		<#--STRONGHOLDS-->[],
		<#--UNDERGROUND_ORES-->[
		<#if data.defaultFeatures?contains("Ores")>
			"minecraft:ore_dirt",
      		"minecraft:ore_gravel",
      		"minecraft:ore_granite_upper",
      		"minecraft:ore_granite_lower",
      		"minecraft:ore_diorite_upper",
      		"minecraft:ore_diorite_lower",
      		"minecraft:ore_andesite_upper",
      		"minecraft:ore_andesite_lower",
      		"minecraft:ore_tuff",
      		"minecraft:ore_coal_upper",
      		"minecraft:ore_coal_lower",
      		"minecraft:ore_iron_upper",
      		"minecraft:ore_iron_middle",
      		"minecraft:ore_iron_small",
      		"minecraft:ore_gold",
      		"minecraft:ore_gold_lower",
      		"minecraft:ore_redstone",
      		"minecraft:ore_redstone_lower",
      		"minecraft:ore_diamond",
      		"minecraft:ore_diamond_large",
      		"minecraft:ore_diamond_buried",
      		"minecraft:ore_lapis",
      		"minecraft:ore_lapis_buried",
      		"minecraft:ore_copper"
		</#if>
    	],
		<#--UNDERGROUND_DECORATION-->[],
		<#--FLUID_SPRINGS-->[],
		<#--VEGETAL_DECORATION-->[],
		<#--TOP_LAYER_MODIFICATION-->[
			"minecraft:freeze_top_layer"
    	]
    ]
}

<#function listStructures>
	<#assign retval = []>
	<#if data.spawnWoodlandMansion><#assign retval = retval + ["minecraft:mansion"] /></#if>
	<#if data.spawnMineshaft><#assign retval = retval + ["minecraft:mineshaft_mesa"] /></#if>
	<#if data.spawnMineshaftMesa><#assign retval = retval + ["minecraft:mineshaft"] /></#if>
	<#if data.spawnStronghold><#assign retval = retval + ["minecraft:stronghold"] /></#if>
	<#if data.spawnPillagerOutpost><#assign retval = retval + ["minecraft:pillager_outpost"] /></#if>
	<#if data.spawnShipwreck><#assign retval = retval + ["minecraft:shipwreck"] /></#if>
	<#if data.spawnShipwreckBeached><#assign retval = retval + ["minecraft:shipwreck_beached"] /></#if>
	<#if data.spawnSwampHut><#assign retval = retval + ["minecraft:swamp_hut"] /></#if>
	<#if data.oceanRuinType != "NONE"><#assign retval = retval + ["minecraft:ocean_ruin_${data.oceanRuinType?lower_case}"] /></#if>
	<#if data.spawnOceanMonument><#assign retval = retval + ["minecraft:monument"] /></#if>
	<#if data.spawnDesertPyramid><#assign retval = retval + ["minecraft:desert_pyramid"] /></#if>
	<#if data.spawnJungleTemple><#assign retval = retval + ["minecraft:jungle_pyramid"] /></#if>
	<#if data.spawnIgloo><#assign retval = retval + ["minecraft:igloo"] /></#if>
	<#if data.spawnBuriedTreasure><#assign retval = retval + ["minecraft:buried_treasure"] /></#if>
	<#if data.spawnNetherBridge><#assign retval = retval + ["minecraft:buried_treasure"] /></#if>
	<#if data.spawnNetherFossil><#assign retval = retval + ["minecraft:buried_treasure"] /></#if>
	<#if data.spawnBastionRemnant><#assign retval = retval + ["minecraft:buried_treasure"] /></#if>
	<#if data.spawnEndCity><#assign retval = retval + ["minecraft:buried_treasure"] /></#if>
	<#if data.spawnRuinedPortal != "NONE"><#assign retval = retval + ["minecraft:ruined_portal_${data.spawnRuinedPortal?lower_case}"] /></#if>
	<#if data.villageType != "none"><#assign retval = retval + ["minecraft:village_${data.villageType}"] /></#if>
	<#return retval>
</#function>

<#function getEntitiesOfType entityList type>
	<#assign retval = []>
	<#list entityList as entity>
		<#if entity.spawnType == type>
			<#assign retval = retval + [entity]>
		</#if>
	</#list>
	<#return retval>
</#function>

<#macro generateEntityList entityList type>
	<#assign entities = getEntitiesOfType(entityList, type)>
	<#list entities as entry>
	<#-- @formatter:off -->
    {
		"type": "${entry.entity}",
		"weight": ${entry.weight},
		"minCount": ${entry.minGroup},
		"maxCount": ${entry.maxGroup}
	}<#if entry?has_next>,</#if>
	<#-- @formatter:on -->
    </#list>
</#macro>
<#-- @formatter:on -->
