{
  "type": "weighted_list",
  "distribution": [
    <#list input_list$entry as entry>
    {
      "data": ${entry},
      "weight": ${field_list$weight[entry?index]}
    }
    <#sep>,</#list>
  ]
}