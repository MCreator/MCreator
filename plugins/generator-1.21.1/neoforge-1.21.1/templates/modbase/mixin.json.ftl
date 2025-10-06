<#assign mixins = []>
<#assign clientMixins = []>
<#if w.getGElementsOfType('biome')?filter(e -> e.spawnBiome || e.spawnInCaves || e.spawnBiomeNether)?size != 0>
	<#assign mixins = mixins + ['NoiseGeneratorSettingsMixin']>
</#if>
<#if w.getGElementsOfType('armor')?filter(e -> e.helmetCanFly || e.leggingsCanFly || e.bootsCanFly)?size != 0>
	<#assign mixins = mixins + ['PlayerMixin', 'LivingEntityMixin']>
	<#assign clientMixins = clientMixins + ['LocalPlayerMixin']>
</#if>

{
  "required": true,
  "package": "${package}.mixin",
  "compatibilityLevel": "JAVA_21",
  "mixins": [
	<#list mixins as mixin>"${mixin}"<#sep>,</#list>
  ],
  "client": [
	<#list clientMixins as mixin>"${mixin}"<#sep>,</#list>
  ],
  "injectors": {
    "defaultRequire": 1
  },
  "minVersion": "0.8.4"
}