{
  "type": "minecraft:all_of",
  "predicates": [
  <#list input_list$condition as condition>
    ${condition}
  <#sep>,</#list>
  ]
}