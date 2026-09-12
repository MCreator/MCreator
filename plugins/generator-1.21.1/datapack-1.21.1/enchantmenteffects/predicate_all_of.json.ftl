{
  "condition": "minecraft:all_of",
  "terms": [
  <#list input_list$condition as condition>
    ${condition}
  <#sep>,</#list>
  ]
}