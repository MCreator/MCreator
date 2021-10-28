<#-- @formatter:off -->
<#macro procedureCode object dependencies={}>
    <#compress>
    ${object.getName()}Procedure.execute(ImmutableMap.<String, Object>builder()
    <#assign deps = [] />
    <#list object.getDependencies(generator.getWorkspace()) as dependency>
        <#assign deps += [dependency.getName()] />
    </#list>
    <#list dependencies as name, value>
        <#if deps?seq_contains(name)>.put("${name}", ${value})</#if>
    </#list>
    .build());
    </#compress>
</#macro>

<#macro procedureToRetvalCode name dependencies customVals={}>
    <#assign depsBuilder = []>

    <#list dependencies as dependency>
        <#if !customVals[dependency.getName()]?? >
            <#assign depsBuilder += ["\"" + dependency.getName() + "\", " + dependency.getName()]>
        </#if>
    </#list>

    <#list customVals as key, value>
        <#assign depsBuilder += ["\"" + key + "\", " + value]>
    </#list>

    ${(name)}Procedure.execute(ImmutableMap.<String, Object>builder()<#list depsBuilder as dep>.put(${dep})</#list>.build())
</#macro>

<#macro procedureToCode name dependencies customVals={}>
    <@procedureToRetvalCode name dependencies customVals/>;
</#macro>

<#macro procedureOBJToCode object="">
    <#if hasProcedure(object)>
        <@procedureToCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    </#if>
</#macro>

<#macro procedureOBJToConditionCode object="">
    <#if hasProcedure(object)>
        <@procedureToRetvalCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    <#else>
        true
    </#if>
</#macro>

<#macro procedureOBJToNumberCode object="">
    <#if hasProcedure(object)>
        <@procedureToRetvalCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    <#else>
        0
    </#if>
</#macro>

<#macro procedureOBJToItemstackCode object="">
    <#if hasProcedure(object)>
        /*@ItemStack*/ <@procedureToRetvalCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    <#else>
        /*@ItemStack*/ ItemStack.EMPTY
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

<#function hasReturnValue object="">
    <#return hasProcedure(object) && object.hasReturnValue(generator.getWorkspace())>
</#function>

<#-- @formatter:on -->