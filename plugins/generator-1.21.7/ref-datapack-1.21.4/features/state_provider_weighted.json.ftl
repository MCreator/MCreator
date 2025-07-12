/*@BlockStateProvider*/{
  "type": "minecraft:weighted_state_provider",
  "entries": [
    <#list input_list$entry as entry>
    {
      "data": ${entry},
      "weight": ${field_list$weight[entry?index]}
    }
    <#sep>,</#list>
  ]
}