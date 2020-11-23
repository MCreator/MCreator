<#if scope == "GLOBAL_SESSION">
    <#if type == "ITEMSTACK">
        /*@ItemStack*/(${JavaModName}Variables.${name})
    <#else>
        (${JavaModName}Variables.${name})
    </#if>
<#elseif scope == "GLOBAL_WORLD">
    <#if type == "ITEMSTACK">
        /*@ItemStack*/(${JavaModName}Variables.WorldVariables.get(world).${name})
    <#else>
        (${JavaModName}Variables.WorldVariables.get(world).${name})
    </#if>
<#elseif scope == "GLOBAL_MAP">
    <#if type == "ITEMSTACK">
        /*@ItemStack*/(${JavaModName}Variables.MapVariables.get(world).${name})
    <#else>
        (${JavaModName}Variables.MapVariables.get(world).${name})
    </#if>
<#elseif scope == "PLAYER_LIFETIME" || scope == "PLAYER_PERSISTENT">
    <#if type == "ITEMSTACK">
        /*@ItemStack*/((entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null)
            .orElse(new ${JavaModName}Variables.PlayerVariables())).${name})
    <#else>
        ((entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null)
            .orElse(new ${JavaModName}Variables.PlayerVariables())).${name})
    </#if>
<#elseif scope == "local">
    <#if type == "ITEMSTACK">
        /*@ItemStack*/(${name}[0])
    <#else>
        (${name}[0])
    </#if>
</#if>