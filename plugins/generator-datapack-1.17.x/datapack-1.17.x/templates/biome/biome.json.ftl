<#-- @formatter:off -->
{
    "scale": ${data.heightVariation},
    "depth": ${data.baseHeight},
    "precipitation": <#if (data.rainingPossibility > 0)><#if (data.temperature > 0.15)>"rain"<#else>"snow"</#if><#else>"none"</#if>,
    "temperature": ${data.temperature},
    "downfall": ${data.rainingPossibility},
    "category": "${data.biomeCategory?replace("THEEND", "THE_END")?lower_case}",
	"surface_builder": "${modid}:${registryname}",
	"spawn_costs": {},
    "player_spawn_friendly": true,
	<#if data.parent?? && data.parent.getUnmappedValue() != "No parent">
	"parent": "${data.parent}",
	</#if>
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
			"minecraft:ore_granite",
			"minecraft:ore_diorite",
			"minecraft:ore_andesite",
			"minecraft:ore_coal",
			"minecraft:ore_iron",
			"minecraft:ore_gold",
			"minecraft:ore_redstone",
			"minecraft:ore_diamond",
			"minecraft:ore_lapis"
		</#if>
    	],
		<#--UNDERGROUND_DECORATION-->[],
		<#--VEGETAL_DECORATION-->[],
		<#--TOP_LAYER_MODIFICATION-->[
			"minecraft:freeze_top_layer"
    	]
    ],
    "starts": [
		<#list listStructures() as start>
			"${start}"<#if start?has_next>,</#if>
		</#list>
    ]
}

<#function listStructures>
	<#assign retval = []>
	<#if data.spawnWoodlandMansion><#assign retval = retval + ["minecraft:mansion"] /></#if>
	<#if data.spawnMineshaft><#assign retval = retval + ["minecraft:mineshaft"] /></#if>
	<#if data.spawnStronghold><#assign retval = retval + ["minecraft:stronghold"] /></#if>
	<#if data.spawnPillagerOutpost><#assign retval = retval + ["minecraft:pillager_outpost"] /></#if>
	<#if data.spawnShipwreck><#assign retval = retval + ["minecraft:shipwreck"] /></#if>
	<#if data.oceanRuinType != "NONE"><#assign retval = retval + ["minecraft:ocean_ruin_${data.oceanRuinType?lower_case}"] /></#if>
	<#if data.spawnOceanMonument><#assign retval = retval + ["minecraft:monument"] /></#if>
	<#if data.spawnDesertPyramid><#assign retval = retval + ["minecraft:desert_pyramid"] /></#if>
	<#if data.spawnJungleTemple><#assign retval = retval + ["minecraft:jungle_pyramid"] /></#if>
	<#if data.spawnIgloo><#assign retval = retval + ["minecraft:igloo"] /></#if>
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
