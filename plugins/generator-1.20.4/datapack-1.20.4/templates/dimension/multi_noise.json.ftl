<#-- @formatter:off -->

<#assign biomesmap = fp.file("utils/defaultbiomes.json")?eval_json/>

<#macro multiNoiseSource>
{
    "type": "minecraft:multi_noise",
    "biomes": [
        <#list w.filterBrokenReferences(data.biomesInDimension) as biome>
        {
            "biome": "${biome}",
            "parameters":
            <#if data.biomesInDimension?size == 1>
            {
                "temperature": 0,
                "humidity": 0,
                "continentalness": 0,
                "weirdness": 0,
                "erosion": 0,
                "depth": 0,
                "offset": 0
            }
            <#else>
                <#if biome.getUnmappedValue().startsWith("CUSTOM:")>
                    <#assign ge = w.getWorkspace().getModElementByName(biome.getUnmappedValue().replace("CUSTOM:", "")).getGeneratableElement()/>
                    {
                        "temperature": [${ge.genTemperature.min}, ${ge.genTemperature.max}],
                        "humidity": [${ge.genHumidity.min}, ${ge.genHumidity.max}],
                        "continentalness": [${ge.genContinentalness.min}, ${ge.genContinentalness.max}],
                        "weirdness": [${ge.genWeirdness.min}, ${ge.genWeirdness.max}],
                        "erosion": [${ge.genErosion.min}, ${ge.genErosion.max}],
                        "depth": 0, <#-- 0 for surface biomes, 1 for cave biomes -->
                        "offset": 0
                    }
                <#else>
                    <#if biomesmap["minecraft:" + biome.toString()]??>
                        ${thelper.obj2str(biomesmap["minecraft:" + biome.toString()])}
                    <#else>
                    {
                        "temperature": 0,
                        "humidity": 0,
                        "continentalness": 0,
                        "weirdness": 0,
                        "erosion": 0,
                        "depth": 0,
                        "offset": 0
                    }
                    </#if>
                </#if>
            </#if>
        }
        <#if biome?has_next>,</#if>
      </#list>
    ]
}
</#macro>
<#-- @formatter:on -->