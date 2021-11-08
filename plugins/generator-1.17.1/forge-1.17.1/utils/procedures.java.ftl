<#-- @formatter:off -->
<#macro procedureCode object dependencies={}>
    <#compress>

    <#assign deps_filtered = {} />
    <#list object.getDependencies(generator.getWorkspace()) as dependency>
        <#list dependencies as name, value>
            <#if dependency.getName() == name>
                <#assign deps_filtered += {name: value} />
            </#if>
        </#list>
    </#list>

    <#if deps_filtered?size == 0>
        ${object.getName()}Procedure.execute(Collections.EMPTY_MAP);
    <#else>
        ${object.getName()}Procedure.execute(Stream.of(
        <#list deps_filtered as name, value>new AbstractMap.SimpleEntry<>("${name}", ${value})<#if name?has_next>,</#if></#list>
        ).collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll));
    </#if>
    </#compress>
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
        <#if !customVals[dependency.getName()]?? >
            <#assign depsBuilder += ["\"" + dependency.getName() + "\", " + dependency.getName()]>
        </#if>
    </#list>

    <#list customVals as key, value>
        <#assign depsBuilder += ["\"" + key + "\", " + value]>
    </#list>

    <#if depsBuilder?size == 0>
        ${(name)}Procedure.execute(Collections.EMPTY_MAP)
    <#else>
        ${(name)}Procedure.execute(Stream.of(
        <#list depsBuilder as dep>new AbstractMap.SimpleEntry<>(${dep})<#if dep?has_next>,</#if></#list>
        ).collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll))
    </#if>
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

<#function hasReturnValueOf object="" type="">
    <#return hasProcedure(object) && (object.getReturnValueType(generator.getWorkspace()) == type)>
</#function>

<#-- @formatter:on -->