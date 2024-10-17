<#if scope == "GLOBAL_SESSION">
    <#if type == "NUMBER">
        ${JavaModName}Variables.${name} =(double)${value};
    <#elseif type == "LOGIC">
        ${JavaModName}Variables.${name} =(boolean)${value};
    <#elseif type == "STRING">
        ${JavaModName}Variables.${name} =(String)${value};
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
    </#if>
<#elseif scope == "local">
    <#if type == "NUMBER">
        ${name} =(double)${value};
    <#elseif type == "LOGIC">
        ${name} =(boolean)${value};
    <#elseif type == "STRING">
        ${name} =(String)${value};
    </#if>
</#if>