{
  "type": "minecraft:matching_fluids",
  "fluids": "${generator.map(field$fluid, "fluids", 1)}"
  <#if (field$x != "0")||(field$y != "0")||(field$z != "0")>,
  "offset": [
    ${field$x},
    ${field$y},
    ${field$z}
  ]</#if>
}