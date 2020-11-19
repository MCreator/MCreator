<#if scope == "GLOBAL_SESSION">
    <#if type == "ITEMSTACK">
        /*@ItemStack*/(${JavaModName}Variables.${name})
    <#elseif type == "BLOCKSTATE">
        /*@BlockState*/(${JavaModName}Variables.${name})
    <#else>
        (${JavaModName}Variables.${name})
    </#if>
<#elseif scope == "GLOBAL_WORLD">
    <#if type == "ITEMSTACK">
        /*@ItemStack*/(${JavaModName}Variables.WorldVariables.get(world).${name})
    <#elseif type == "BLOCKSTATE">
        /*@BlockState*/(${JavaModName}Variables.WorldVariables.get(world).${name})
    <#else>
        (${JavaModName}Variables.WorldVariables.get(world).${name})
    </#if>
<#elseif scope == "GLOBAL_MAP">
    <#if type == "ITEMSTACK">
        /*@ItemStack*/(${JavaModName}Variables.MapVariables.get(world).${name})
    <#elseif type == "BLOCKSTATE">
        /*@BlockState*/(${JavaModName}Variables.MapVariables.get(world).${name})
    <#else>
        (${JavaModName}Variables.MapVariables.get(world).${name})
    </#if>
<#elseif scope == "PLAYER_LIFETIME" || scope == "PLAYER_PERSISTENT">
    <#if type == "ITEMSTACK">
        /*@ItemStack*/((entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null)
            .orElse(new ${JavaModName}Variables.PlayerVariables())).${name})
    <#elseif type == "BLOCKSTATE">
        /*@BlockState*/((entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null)
            .orElse(new ${JavaModName}Variables.PlayerVariables())).${name})
    <#else>
        ((entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null)
            .orElse(new ${JavaModName}Variables.PlayerVariables())).${name})
    </#if>
<#elseif scope == "local">
    <#if type == "ITEMSTACK">
        /*@ItemStack*/(${name})
    <#elseif type == "BLOCKSTATE">
        /*@BlockState*/(${name})
    <#else>
        (${name})
    </#if>
</#if>