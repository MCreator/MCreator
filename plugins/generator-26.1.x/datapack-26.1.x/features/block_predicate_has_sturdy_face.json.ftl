{
  "type": "minecraft:has_sturdy_face",
  "direction": "${generator.map(field$direction, "directions", 1)}"
  <#if (field$x != "0")||(field$y != "0")||(field$z != "0")>,
  "offset": [
    ${field$x},
    ${field$y},
    ${field$z}
  ]</#if>
}