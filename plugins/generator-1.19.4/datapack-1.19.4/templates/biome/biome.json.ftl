<#-- @formatter:off -->

<#assign features_carvers = []>
<#assign features_raw_generation = []>
<#assign features_lakes = []>
<#assign features_local_modifications = []>
<#assign features_underground_structures = []>
<#assign features_surface_structures = []>
<#assign features_strongholds = []>
<#assign features_underground_ores = []>
<#assign features_underground_decorations = []>
<#assign features_fluid_springs = []>
<#assign features_vegetal_decoration = []>
<#assign features_top_layer_modification = []>

<#if data.hasTrees()>
	<#assign features_vegetal_decoration = features_vegetal_decoration + ["${modid}:${registryname}_tree"]>
</#if>

<#list generator.sortByMappings(data.defaultFeatures, "defaultfeatures") as defaultFeature>
	<#if data.spawnBiomeNether &&
	(defaultFeature == "Caves" ||
	defaultFeature == "ExtraEmeraldOre" ||
	defaultFeature == "ExtraGoldOre" ||
	defaultFeature == "Ores" ||
	defaultFeature == "MonsterRooms" ||
	defaultFeature == "Fossils")>
		<#continue>
	</#if>

	<#assign mfeat = generator.map(defaultFeature, "defaultfeatures")>
	<#if mfeat != "null">
		<#assign features_array = mfeat?split(",")>
		<#list features_array as feature>
			<#assign feature_stage = feature?trim?split("/")[0]>
			<#assign feature_name = feature?trim?split("/")[1]>
			<#if feature_stage == "carvers">
				<#assign features_carvers = features_carvers + ["minecraft:" + feature_name]>
			<#elseif feature_stage == "raw_generation">
				<#assign features_raw_generation = features_raw_generation + ["minecraft:" + feature_name]>
			<#elseif feature_stage == "lakes">
				<#assign features_lakes = features_lakes + ["minecraft:" + feature_name]>
			<#elseif feature_stage == "local_modifications">
				<#assign features_local_modifications = features_local_modifications + ["minecraft:" + feature_name]>
			<#elseif feature_stage == "underground_structures">
				<#assign features_underground_structures = features_underground_structures + ["minecraft:" + feature_name]>
			<#elseif feature_stage == "surface_structures">
				<#assign features_surface_structures = features_surface_structures + ["minecraft:" + feature_name]>
			<#elseif feature_stage == "strongholds">
				<#assign features_strongholds = features_strongholds + ["minecraft:" + feature_name]>
			<#elseif feature_stage == "underground_ores">
				<#assign features_underground_ores = features_underground_ores + ["minecraft:" + feature_name]>
			<#elseif feature_stage == "underground_decorations">
				<#assign features_underground_decorations = features_underground_decorations + ["minecraft:" + feature_name]>
			<#elseif feature_stage == "fluid_springs">
				<#assign features_fluid_springs = features_fluid_springs + ["minecraft:" + feature_name]>
			<#elseif feature_stage == "vegetal_decoration">
				<#assign features_vegetal_decoration = features_vegetal_decoration + ["minecraft:" + feature_name]>
			<#elseif feature_stage == "top_layer_modification">
				<#assign features_top_layer_modification = features_top_layer_modification + ["minecraft:" + feature_name]>
			</#if>
		</#list>
	</#if>
</#list>

<#-- now in dimension: surface and underground block -->
{
    "has_precipitation": ${(data.rainingPossibility > 0)?c},
    "temperature": ${data.temperature},
    "downfall": ${data.rainingPossibility},
    "effects": {
		<#if data.ambientSound?has_content && data.ambientSound.getMappedValue()?has_content>
		"ambient_sound": "${data.ambientSound}",
		</#if>
	  	<#if data.moodSound?has_content && data.moodSound.getMappedValue()?has_content>
		"mood_sound": {
			"sound": "${data.moodSound}",
			"tick_delay": ${data.moodSoundDelay},
			"offset": 8,
			"block_search_extent": 2
		},
	  	</#if>
	  	<#if data.additionsSound?has_content && data.additionsSound.getMappedValue()?has_content>
		"additions_sound": {
			"sound": "${data.additionsSound}",
		  	"tick_chance": 0.0111
		},
	  	</#if>
	  	<#if data.music?has_content && data.music.getMappedValue()?has_content>
		"music": {
			"sound": "${data.music}",
			"min_delay": 12000,
			"max_delay": 24000,
			"replace_current_music": true
        },
	  	</#if>
	  	<#if data.spawnParticles>
		"particle": {
			"options": {
				"type": "${data.particleToSpawn.getMappedValue(1)}"
			},
			"probability": ${data.particlesProbability / 100}
        },
		</#if>
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
      	"axolotls": [<@generateEntityList data.spawnEntries "axolotls"/>],
      	"underground_water_creature": [<@generateEntityList data.spawnEntries "undergroundWaterCreature"/>],
      	"water_creature": [<@generateEntityList data.spawnEntries "waterCreature"/>],
		"water_ambient": [<@generateEntityList data.spawnEntries "waterAmbient"/>],
		"misc": [<@generateEntityList data.spawnEntries "misc"/>]
	},
	"spawn_costs": {},
    "carvers": {
		<#if features_carvers?has_content>
    	"air": [<#list features_carvers as feature>"${feature}"<#sep>,</#list>]
		</#if>
    },
    "features": [
    	<#--RAW_GENERATION-->[<#list thelper.removeDuplicates(features_raw_generation) as feature>"${feature}"<#sep>,</#list>],
		<#--LAKES-->[<#list thelper.removeDuplicates(features_lakes) as feature>"${feature}"<#sep>,</#list>],
		<#--LOCAL_MODIFICATIONS-->[<#list thelper.removeDuplicates(features_local_modifications) as feature>"${feature}"<#sep>,</#list>],
		<#--UNDERGROUND_STRUCTURES-->[<#list thelper.removeDuplicates(features_underground_structures) as feature>"${feature}"<#sep>,</#list>],
		<#--SURFACE_STRUCTURES-->[<#list thelper.removeDuplicates(features_surface_structures) as feature>"${feature}"<#sep>,</#list>],
		<#--STRONGHOLDS-->[<#list thelper.removeDuplicates(features_strongholds) as feature>"${feature}"<#sep>,</#list>],
		<#--UNDERGROUND_ORES-->[<#list thelper.removeDuplicates(features_underground_ores) as feature>"${feature}"<#sep>,</#list>],
		<#--UNDERGROUND_DECORATION-->[<#list thelper.removeDuplicates(features_underground_decorations) as feature>"${feature}"<#sep>,</#list>],
		<#--FLUID_SPRINGS-->[<#list thelper.removeDuplicates(features_fluid_springs) as feature>"${feature}"<#sep>,</#list>],
		<#--VEGETAL_DECORATION-->[<#list thelper.removeDuplicates(features_vegetal_decoration) as feature>"${feature}"<#sep>,</#list>],
		<#--TOP_LAYER_MODIFICATION-->[<#list thelper.removeDuplicates(features_top_layer_modification) as feature>"${feature}"<#sep>,</#list>]
    ]
}

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
		"type": "${entry.entity.getMappedValue(2)}",
		"weight": ${entry.weight},
		"minCount": ${entry.minGroup},
		"maxCount": ${entry.maxGroup}
	}<#if entry?has_next>,</#if>
	<#-- @formatter:on -->
    </#list>
</#macro>

<#-- @formatter:on -->
