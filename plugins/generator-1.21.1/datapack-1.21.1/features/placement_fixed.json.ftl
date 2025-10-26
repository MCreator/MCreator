{
  "type": "fixed_placement",
  "positions": [
  <#if field_list$x?size != 0>
    <#list 0..field_list$x?size-1 as i>
    [ ${field_list$x[i]}, ${field_list$y[i]}, ${field_list$z[i]} ]
    <#sep>,</#list>
  </#if>
  ]
},