{
  "name": "${modid}:${registryname}",
  "fallback": "minecraft:empty",
  "elements": [
    {
      "weight": 1,
      "element": {
        "element_type": "minecraft:single_pool_element",
        "location": "${modid}:${data.structure}",
        "projection": "${data.projection}",
        "processors": {
          "processors": [
            {
              "processor_type": "minecraft:block_ignore",
              "blocks": [
                <#if data.ignoreBlocks == "STRUCTURE_BLOCK">
                {
                  "Name": "minecraft:structure_block"
                }
                <#elseif data.ignoreBlocks == "AIR_AND_STRUCTURE_BLOCK">
                {
                  "Name": "minecraft:structure_block"
                },
                {
                  "Name": "minecraft:air"
                }
                <#elseif data.ignoreBlocks == "AIR">
                {
                  "Name": "minecraft:air"
                }
                </#if>
              ]
            }
          ]
        }
      }
    }
  ]
}