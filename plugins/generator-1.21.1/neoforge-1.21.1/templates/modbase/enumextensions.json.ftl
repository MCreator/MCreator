<#assign raiders = w.getGElementsOfType("livingentity")?filter(e -> e.mobBehaviourType == "Raider")>
<#assign boats = w.getGElementsOfType("specialentity")>
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
	</#list><#if (raiders?size > 0) && (boats?size > 0)>,</#if>
    <#list boats as boat>
	{
      "enum": "net/minecraft/world/entity/vehicle/Boat$Type",
      "name": "${modid?upper_case}_${boat.getModElement().getRegistryNameUpper()}",
      "constructor": "(Ljava/util/function/Supplier;Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/Supplier;Ljava/util/function/Supplier;Z)V",
      "parameters": {
        "class": "${package?replace(".", "/")}/init/${JavaModName}BoatTypes",
        "field": "${boat.getModElement().getRegistryNameUpper()}_TYPE"
      }
    }<#sep>,
	</#list>
  ]
}