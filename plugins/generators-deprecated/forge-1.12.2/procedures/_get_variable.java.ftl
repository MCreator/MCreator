<#if scope == "GLOBAL_SESSION">
        (${JavaModName}Variables.${name})
<#elseif scope == "GLOBAL_WORLD">
        (${JavaModName}Variables.WorldVariables.get(world).${name})
<#elseif scope == "GLOBAL_MAP">
        (${JavaModName}Variables.MapVariables.get(world).${name})
<#elseif scope == "local">
        (${name})
</#if>