/*@BlockStateProvider*/{
  "type": "minecraft:weighted",
  "entries": [
    <#list input_list$entry as entry>
    {
      "data": ${entry},
      "weight": ${field_list$weight[entry?index]}
    }
    <#sep>,</#list>
  ]
}