<#assign raiders = w.getGElementsOfType("livingentity")?filter(e -> e.mobBehaviourType == "Raider")>
{
  "entries": [
    <#list raiders as raider>
	{
      "enum": "net/minecraft/world/entity/raid/Raid$RaiderType",
      "name": "${modid?upper_case}_${raider.getModElement().getRegistryNameUpper()}",
      "constructor": "(Ljava/util/function/Supplier;[I)V",
      "parameters": {
        "class": "${package?replace(".", "/")}/entity/${raider.getModElement().getName()}Entity",
        "field": "RAIDER_TYPE"
      }
    }<#sep>,
	</#list>
  ]
}