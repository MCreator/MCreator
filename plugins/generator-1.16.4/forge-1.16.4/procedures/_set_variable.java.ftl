<#include "mcitems.ftl">
<#if scope == "GLOBAL_SESSION">
    <#if type == "NUMBER">
        ${JavaModName}Variables.${name} =(double)${value};
    <#elseif type == "LOGIC">
        ${JavaModName}Variables.${name} =(boolean)${value};
    <#elseif type == "STRING">
        ${JavaModName}Variables.${name} =(String)${value};
    <#elseif type == "ITEMSTACK">
        ${JavaModName}Variables.${name} =${mappedMCItemToItemStackCode(value, 1)};
    </#if>
<#elseif scope == "GLOBAL_WORLD">
    <#if type == "NUMBER">
        ${JavaModName}Variables.WorldVariables.get(world).${name} =(double)${value};
        ${JavaModName}Variables.WorldVariables.get(world).syncData(world);
    <#elseif type == "LOGIC">
        ${JavaModName}Variables.WorldVariables.get(world).${name} =(boolean)${value};
        ${JavaModName}Variables.WorldVariables.get(world).syncData(world);
    <#elseif type == "STRING">
        ${JavaModName}Variables.WorldVariables.get(world).${name} =(String)${value};
        ${JavaModName}Variables.WorldVariables.get(world).syncData(world);
    <#elseif type == "ITEMSTACK">
        ${JavaModName}Variables.WorldVariables.get(world).${name} =${mappedMCItemToItemStackCode(value, 1)};
        ${JavaModName}Variables.WorldVariables.get(world).syncData(world);
    </#if>
<#elseif scope == "GLOBAL_MAP">
    <#if type == "NUMBER">
        ${JavaModName}Variables.MapVariables.get(world).${name} =(double)${value};
        ${JavaModName}Variables.MapVariables.get(world).syncData(world);
    <#elseif type == "LOGIC">
        ${JavaModName}Variables.MapVariables.get(world).${name} =(boolean)${value};
        ${JavaModName}Variables.MapVariables.get(world).syncData(world);
    <#elseif type == "STRING">
        ${JavaModName}Variables.MapVariables.get(world).${name} =(String)${value};
        ${JavaModName}Variables.MapVariables.get(world).syncData(world);
    <#elseif type == "ITEMSTACK">
        ${JavaModName}Variables.MapVariables.get(world).${name} =${mappedMCItemToItemStackCode(value, 1)};
        ${JavaModName}Variables.MapVariables.get(world).syncData(world);
    </#if>
<#elseif scope == "PLAYER_LIFETIME" || scope == "PLAYER_PERSISTENT">
    <#if type == "NUMBER">
    {
        double _setval = (double)${value};
        entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
        	capability.${name} = _setval;
        	capability.syncPlayerVariables(entity);
		});
    }
    <#elseif type == "LOGIC">
    {
        boolean _setval = (boolean)${value};
        entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
        	capability.${name} = _setval;
        	capability.syncPlayerVariables(entity);
		});
    }
    <#elseif type == "STRING">
    {
        String _setval = (String)${value};
        entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
        	capability.${name} = _setval;
        	capability.syncPlayerVariables(entity);
		});
    }
    <#elseif type == "ITEMSTACK">
    {
        ItemStack _setval = ${mappedMCItemToItemStackCode(value, 1)};
        entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
        	capability.${name} = _setval;
        	capability.syncPlayerVariables(entity);
		});
    }
    </#if>
<#elseif scope == "local">
    <#if type == "NUMBER">
        ${name} =(double)${value};
    <#elseif type == "LOGIC">
        ${name} =(boolean)${value};
    <#elseif type == "STRING">
        ${name} =(String)${value};
    <#elseif type == "ITEMSTACK">
        ${name} = ${mappedMCItemToItemStackCode(value, 1)};
    </#if>
</#if>