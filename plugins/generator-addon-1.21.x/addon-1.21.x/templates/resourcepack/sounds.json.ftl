<#assign individual_named_sounds = []>
<#list bebiomes as biome>
  <#if biome.ambientSound?has_content && biome.ambientSound.getMappedValue()?has_content>
    <#assign individual_named_sounds += [biome.ambientSound]>
  </#if>
  <#if biome.additionsSound?has_content && biome.additionsSound.getMappedValue()?has_content>
    <#assign individual_named_sounds += [biome.additionsSound]>
  </#if>
  <#if biome.moodSound?has_content && biome.moodSound.getMappedValue()?has_content>
    <#assign individual_named_sounds += [biome.moodSound]>
  </#if>
</#list>
{
  "individual_named_sounds": {
    "sounds": {
      <#list individual_named_sounds as sound>
        "${sound}": {
          "pitch": 1,
          "sound": "${sound}",
          "volume": 1
        }<#sep>,
      </#list>
    }
  }
}