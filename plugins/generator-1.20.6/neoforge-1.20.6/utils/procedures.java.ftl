<#-- @formatter:off -->
<#macro procedureDependenciesCode requiredDependencies dependencies={}>
<#compress>
    <#assign deps_filtered = [] />
    <#list requiredDependencies as dependency>
        <#list dependencies as name, value>
            <#if dependency.getName() == name>
                <#assign deps_filtered += [value] />
            </#if>
        </#list>
    </#list>

    <#list deps_filtered as value>${value}<#if value?has_next>,</#if></#list>
</#compress>
</#macro>

<#macro procedureCode object dependencies={} semicolon=true>
    ${object.getName()}Procedure.execute(<@procedureDependenciesCode object.getDependencies(generator.getWorkspace()) dependencies/>)<#if semicolon>;</#if>
</#macro>

<#macro procedureCodeWithOptResult object type defaultResult dependencies={}>
    <#if hasReturnValueOf(object, type)>
        return <@procedureCode object dependencies/>
    <#else>
        <@procedureCode object dependencies/>
        return ${defaultResult};
    </#if>
</#macro>

<#macro procedureToRetvalCode name dependencies customVals={}>
    <#assign depsBuilder = []>

    <#list dependencies as dependency>
        <#if !customVals[dependency.getName()]?has_content>
            <#assign depsBuilder += [dependency.getName()]>
        <#else>
            <#assign depsBuilder += [customVals[dependency.getName()]]>
        </#if>
    </#list>

    ${(name)}Procedure.execute(<#list depsBuilder as dep>${dep}<#if dep?has_next>,</#if></#list>)
</#macro>

<#macro procedureToCode name dependencies customVals={}>
    <@procedureToRetvalCode name dependencies customVals/>;
</#macro>

<#macro procedureOBJToCode object="">
    <#if hasProcedure(object)>
        <@procedureToCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    </#if>
</#macro>

<#macro procedureOBJToConditionCode object="" defaultValue=true invertCondition=false>
    <#if hasProcedure(object)>
        <#if invertCondition>!</#if><@procedureToRetvalCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    <#else>
        ${defaultValue?c}
    </#if>
</#macro>

<#macro procedureOBJToNumberCode object="">
    <#if hasProcedure(object)>
        <@procedureToRetvalCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    <#else>
        0
    </#if>
</#macro>

<#macro procedureOBJToStringCode object="">
    <#if hasProcedure(object)>
        <@procedureToRetvalCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    <#else>
        ""
    </#if>
</#macro>

<#macro procedureOBJToItemstackCode object="" addMarker=true>
    <#if addMarker>/*@ItemStack*/</#if>
    <#if hasProcedure(object)>
        <@procedureToRetvalCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    <#else>
        ItemStack.EMPTY
    </#if>
</#macro>

<#macro procedureOBJToInteractionResultCode object="">
    <#if hasProcedure(object)>
        <@procedureToRetvalCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    <#else>
        InteractionResult.PASS
    </#if>
</#macro>

<#function hasProcedure object="">
    <#return object?? && object?has_content && object.getName()?has_content && object.getName() != "null">
</#function>

<#function hasReturnValueOf object="" type="">
    <#return hasProcedure(object) && (object.getReturnValueType(generator.getWorkspace()) == type)>
</#function>

<#-- @formatter:on -->