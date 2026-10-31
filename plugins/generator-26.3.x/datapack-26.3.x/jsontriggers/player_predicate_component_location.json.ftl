"type": "minecraft:location_check",
"predicate": {
  <#list input_list$locationComponent as comp>
    ${comp}<#sep>,
  </#list>
}