<#if type == "itemstack">
    /*@ItemStack*/
<#elseif type == "blockstate">
    /*@BlockState*/
</#if>
<#if scope == "GLOBAL_SESSION">
    (${JavaModName}Variables.${name})
<#elseif scope == "GLOBAL_WORLD">
    (${JavaModName}Variables.WorldVariables.get(world).${name})
<#elseif scope == "GLOBAL_MAP">
    (${JavaModName}Variables.MapVariables.get(world).${name})
<#elseif scope == "PLAYER_LIFETIME" || scope == "PLAYER_PERSISTENT">
    ((entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null)
        .orElse(new ${JavaModName}Variables.PlayerVariables())).${name})
<#elseif scope == "local">
    (${name})
</#if>