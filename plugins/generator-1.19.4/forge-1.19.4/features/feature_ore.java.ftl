new OreConfiguration(List.of(<#list input_list$target as target>${target}<#sep>,</#list>), ${field$size}
    <#if field$discardOnAirChance != "0">, ${field$discardOnAirChance}F</#if>)