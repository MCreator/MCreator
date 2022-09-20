<#-- @formatter:off -->

<#include "../../biomeutils.ftl">

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
                      "temperature": [${temperature2temperature(ge.temperature, normalizeWeight(ge.biomeWeight))}],
                      "humidity": [${rainingPossibility2humidity(ge.rainingPossibility, normalizeWeight(ge.biomeWeight))}],
                      "continentalness": [${baseHeight2continentalness(ge.baseHeight normalizeWeight(ge.biomeWeight))}],
                      "weirdness": [${registryname2weirdness(registryname normalizeWeight(ge.biomeWeight))}],
                      "erosion": [${heightVariation2erosion(ge.heightVariation normalizeWeight(ge.biomeWeight))}],
                      "depth": 0, <#-- 0 for surface biomes, 1 for cave biomes -->
                      "offset": 0
                    }
              <#else>
                ${thelper.obj2str(biomesmap["minecraft:" + biome.toString()])}
              </#if>
          </#if>
        }
        <#if biome?has_next>,</#if>
      </#list>
    ]
}
</#macro>
<#-- @formatter:on -->