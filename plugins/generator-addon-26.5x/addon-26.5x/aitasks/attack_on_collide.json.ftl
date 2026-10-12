"minecraft:behavior.melee_attack": {
    "priority": ${cbi+1},
    "speed_multiplier": ${field$speed},
    <#if field$longmemory == "TRUE">
    "attack_once": false
    <#else>
    "attack_once": true
    </#if>
},