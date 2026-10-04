<#include "../mcitems.ftl">
{
  "values": {
    <#list transformables as block>
    "${modid}:${block.getModElement().getRegistryName()}": {
      "transformer": "minecraft:axe",
      "transform_data": {
        "block_state_provider": {
          "type": "minecraft:rule_based",
          "rules": [
            {
              "if_true": {
                "type": "minecraft:matching_blocks",
                "blocks": "${modid}:${block.getModElement().getRegistryName()}"
              },
              "then": {
                "type": "minecraft:copy_properties",
                "source": {
                  "id": "${mappedMCItemToRegistryName(block.strippingResult)}"
                }
              }
            }
          ]
        },
        "sound": "minecraft:item.axe.strip"
      }
    }<#sep>,</#list>
  }
}
