{
  "values": {
    <#list livingentities?filter(e -> e.spawnInDungeons) as livingentity>
    "${modid}:${livingentity.getModElement().getRegistryName()}": {
      "weight": 100
    }
	<#sep>,</#list>
  }
}