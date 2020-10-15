"minecraft:behavior.melee_attack": {
    "priority": ${customBlockIndex+1},
    "speed_multiplier": ${field$speed},
    <#if field$longmemory?lower_case == "true">
    "attack_once": false
    <#else>
    "attack_once": true
    </#if>
},