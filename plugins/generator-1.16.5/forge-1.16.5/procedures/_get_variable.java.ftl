<#if scope == "GLOBAL_SESSION">
    <#if type == "itemstack">
        /*@ItemStack*/(${JavaModName}Variables.${name})
    <#else>
        (${JavaModName}Variables.${name})
    </#if>
<#elseif scope == "GLOBAL_WORLD">
    <#if type == "itemstack">
        /*@ItemStack*/(${JavaModName}Variables.WorldVariables.get(world).${name})
    <#else>
        (${JavaModName}Variables.WorldVariables.get(world).${name})
    </#if>
<#elseif scope == "GLOBAL_MAP">
    <#if type == "itemstack">
        /*@ItemStack*/(${JavaModName}Variables.MapVariables.get(world).${name})
    <#else>
        (${JavaModName}Variables.MapVariables.get(world).${name})
    </#if>
<#elseif scope == "PLAYER_LIFETIME" || scope == "PLAYER_PERSISTENT">
    <#if type == "itemstack">
        /*@ItemStack*/((entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null)
            .orElse(new ${JavaModName}Variables.PlayerVariables())).${name})
    <#else>
        ((entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null)
            .orElse(new ${JavaModName}Variables.PlayerVariables())).${name})
    </#if>
<#elseif scope == "local">
    <#if type == "itemstack">
        /*@ItemStack*/(${name})
    <#else>
        (${name})
    </#if>
</#if>