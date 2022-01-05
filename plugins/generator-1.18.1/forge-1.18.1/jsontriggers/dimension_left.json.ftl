{
  "trigger": "minecraft:changed_dimension",
  "conditions": {
    <#if field$dimension == "Surface">
    "from": "minecraft:overworld"
    <#elseif field$dimension == "Nether">
    "from": "minecraft:the_nether"
    <#elseif field$dimension == "End">
    "from": "minecraft:the_end"
    <#else>
    "from": "${generator.getResourceLocationForModElement(field$dimension.toString().replace("CUSTOM:", ""))}"
    </#if>
  }
}