<#-- @formatter:off -->

<#macro procedureToCode name dependencies customVals={}>
    {
		Map<String, Object> $_dependencies = new HashMap<>();

        <#list dependencies as dependency>
            <#if !customVals[dependency.getName()]?? >
	    	    $_dependencies.put("${dependency.getName()}",${dependency.getName()});
            </#if>
        </#list>

        <#list customVals as key, value>
        $_dependencies.put("${key}",${value});
        </#list>

        ${(name)}Procedure.executeProcedure($_dependencies);
	}
</#macro>

<#macro procedureToRetvalCode name dependencies customVals={}>
    <#assign depsBuilder = []>

    <#list dependencies as dependency>
        <#if !customVals[dependency.getName()]?? >
            <#assign depsBuilder += ["\"" + dependency.getName() + "\""]>
            <#assign depsBuilder += [dependency.getName()]>
        </#if>
    </#list>

    <#list customVals as key, value>
        <#assign depsBuilder += ["\"" + key + "\""]>
        <#assign depsBuilder += [value]>
    </#list>

    ${(name)}Procedure.executeProcedure(ImmutableMap.of(
        <#list depsBuilder as dep>
            ${dep}<#if dep?has_next>,</#if>
        </#list>
    ))
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

<#macro procedureOBJToActionResultTypeCode object="">
    <#if hasProcedure(object)>
        <@procedureToRetvalCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    <#else>
        ActionResultType.PASS
    </#if>
</#macro>

<#function hasProcedure object="">
    <#return object?? && object?has_content && object.getName()?has_content && object.getName() != "null">
</#function>

<#function hasReturnValue object="">
    <#return hasProcedure(object) && object.hasReturnValue(generator.getWorkspace())>
</#function>

<#-- @formatter:on -->