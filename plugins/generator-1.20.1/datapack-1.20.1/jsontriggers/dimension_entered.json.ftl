{
  "trigger": "minecraft:changed_dimension",
  "conditions": {
    <#if field$dimension == "Surface">
    "to": "minecraft:overworld"
    <#elseif field$dimension == "Nether">
    "to": "minecraft:the_nether"
    <#elseif field$dimension == "End">
    "to": "minecraft:the_end"
    <#else>
    "to": "${generator.getResourceLocationForModElement(field$dimension.toString().replace("CUSTOM:", ""))}"
    </#if>
  }
}