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

<#macro procedureOBJToCode object="">
    <#if object?? && object?has_content && object.getName() != "null">
        <@procedureToCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    </#if>
</#macro>

<#macro procedureToRetvalCode name dependencies type customVals={}>
    <#if (dependencies?size > 5)>
            <@procedureToRetvalCodeManyDependencies name dependencies type customVals />
    <#else>
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
    </#if>
</#macro>

<#macro procedureToRetvalCodeManyDependencies name dependencies type customVals={}>
    (new Object() {
        public ${(type)} $_procedureRetval() {
    	Map<String, Object> $_dependencies = new HashMap<>();

        <#list dependencies as dependency>
            <#if !customVals[dependency.getName()]?? >
        	    $_dependencies.put("${dependency.getName()}",${dependency.getName()});
            </#if>
        </#list>

        <#list customVals as key, value>
        $_dependencies.put("${key}",${value});
        </#list>

        return ${(name)}Procedure.executeProcedure($_dependencies);
        }
    }).$_procedureRetval()
</#macro>

<#macro procedureOBJToConditionCode object="">
    <#if object?? && object?has_content && object.getName() != "null">
        <@procedureToRetvalCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) type="boolean" />
    <#else>
        true
    </#if>
</#macro>

<#macro procedureOBJToItemstackCode object="">
    <#if object?? && object?has_content && object.getName() != "null">
        <@procedureToRetvalCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) type="ItemStack" />
    <#else>
        ItemStack.EMPTY
    </#if>
</#macro>

<#function hasProcedure object="">
    <#return object?? && object?has_content && object.getName() != "null">
</#function>

<#function hasCondition object="">
    <#return object?? && object?has_content && object.getName() != "null">
</#function>

<#-- @formatter:on -->