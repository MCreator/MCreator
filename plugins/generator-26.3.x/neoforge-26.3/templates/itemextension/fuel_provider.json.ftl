{
	"type": "minecraft:div",
	"left": <#if data.hasFuelProcedure()>{
		"type": "${modid}:procedure_fuel",
		"item_extension": "${data.getModElement().getRegistryName()}"
	}<#else>${data.fuelPower.getFixedValue()}</#if>,
	"right": {
		"type": "minecraft:conditional",
		"condition": "minecraft:block/fast_cooking",
		"on_false": "minecraft:cooking/normal_burn_time_reduction_factor",
		"on_true": "minecraft:cooking/fast_burn_time_reduction_factor"
	}
}