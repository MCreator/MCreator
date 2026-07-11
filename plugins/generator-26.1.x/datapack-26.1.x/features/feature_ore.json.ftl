{
  "size": ${field$size},
  "discard_chance_on_air_exposure": ${field$discardOnAirChance},
  "targets": [
  <#list input_list$target as target>
    ${target}
  <#sep>,</#list>
  ]
}