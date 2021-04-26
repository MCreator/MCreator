<#include "mcitems.ftl">
<#if scope == "GLOBAL_SESSION">
    <#if type == "number">
        ${JavaModName}Variables.${name} =(double)${value};
    <#elseif type == "logic">
        ${JavaModName}Variables.${name} =(boolean)${value};
    <#elseif type == "string">
        ${JavaModName}Variables.${name} =(String)${value};
    <#elseif type == "itemstack">
        ${JavaModName}Variables.${name} =${mappedMCItemToItemStackCode(value, 1)};
    </#if>
<#elseif scope == "GLOBAL_WORLD">
    <#if type == "number">
        ${JavaModName}Variables.WorldVariables.get(world).${name} =(double)${value};
        ${JavaModName}Variables.WorldVariables.get(world).syncData(world);
    <#elseif type == "logic">
        ${JavaModName}Variables.WorldVariables.get(world).${name} =(boolean)${value};
        ${JavaModName}Variables.WorldVariables.get(world).syncData(world);
    <#elseif type == "string">
        ${JavaModName}Variables.WorldVariables.get(world).${name} =(String)${value};
        ${JavaModName}Variables.WorldVariables.get(world).syncData(world);
    <#elseif type == "itemstack">
        ${JavaModName}Variables.WorldVariables.get(world).${name} =${mappedMCItemToItemStackCode(value, 1)};
        ${JavaModName}Variables.WorldVariables.get(world).syncData(world);
    </#if>
<#elseif scope == "GLOBAL_MAP">
    <#if type == "number">
        ${JavaModName}Variables.MapVariables.get(world).${name} =(double)${value};
        ${JavaModName}Variables.MapVariables.get(world).syncData(world);
    <#elseif type == "logic">
        ${JavaModName}Variables.MapVariables.get(world).${name} =(boolean)${value};
        ${JavaModName}Variables.MapVariables.get(world).syncData(world);
    <#elseif type == "string">
        ${JavaModName}Variables.MapVariables.get(world).${name} =(String)${value};
        ${JavaModName}Variables.MapVariables.get(world).syncData(world);
    <#elseif type == "itemstack">
        ${JavaModName}Variables.MapVariables.get(world).${name} =${mappedMCItemToItemStackCode(value, 1)};
        ${JavaModName}Variables.MapVariables.get(world).syncData(world);
    </#if>
<#elseif scope == "PLAYER_LIFETIME" || scope == "PLAYER_PERSISTENT">
    <#if type == "number">
    {
        double _setval = (double)${value};
        entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
        	capability.${name} = _setval;
        	capability.syncPlayerVariables(entity);
		});
    }
    <#elseif type == "logic">
    {
        boolean _setval = (boolean)${value};
        entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
        	capability.${name} = _setval;
        	capability.syncPlayerVariables(entity);
		});
    }
    <#elseif type == "string">
    {
        String _setval = (String)${value};
        entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
        	capability.${name} = _setval;
        	capability.syncPlayerVariables(entity);
		});
    }
    <#elseif type == "itemstack">
    {
        ItemStack _setval = ${mappedMCItemToItemStackCode(value, 1)};
        entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
        	capability.${name} = _setval;
        	capability.syncPlayerVariables(entity);
		});
    }
    </#if>
<#elseif scope == "local">
    <#if type == "number">
        ${name} =(double)${value};
    <#elseif type == "logic">
        ${name} =(boolean)${value};
    <#elseif type == "string">
        ${name} =(String)${value};
    <#elseif type == "itemstack">
        ${name} = ${mappedMCItemToItemStackCode(value, 1)};
    </#if>
</#if>