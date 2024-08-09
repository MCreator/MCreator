{
  "type": "minecraft:matching_fluids",
  <#if field$fluid?starts_with("CUSTOM:")>
    "fluids": "${modid}:${field$fluid?ends_with(":Flowing")?then("flowing_","")}${generator.getRegistryNameFromFullName(field$fluid)}"
  <#else>
    "fluids": "${generator.map(field$fluid, "fluids", 1)}"
  </#if>
  <#if (field$x != "0")||(field$y != "0")||(field$z != "0")>,
  "offset": [
    ${field$x},
    ${field$y},
    ${field$z}
  ]</#if>
}