<#macro procedureToCode name dependencies customVals={}>
    {
			java.util.HashMap<String, Object> $_dependencies=new java.util.HashMap<>();
    	<#list dependencies as dependency>
            <#if !customVals[dependency.getName()]?? >
		        $_dependencies.put("${dependency.getName()}",${dependency.getName()});
            </#if>
        </#list>
        <#list customVals as key, value>
            $_dependencies.put("${key}",${value});
        </#list>

		Procedure${(name)}.executeProcedure($_dependencies);
		}
</#macro>

<#macro procedureOBJToCode object="">
    <#if object?? && object?has_content && object.getName() != "null">
        <@procedureToCode name=object.getName() dependencies=object.getDependencies(generator.getWorkspace()) />
    </#if>
</#macro>

<#function hasProcedure object="">
    <#return object?? && object?has_content && object.getName() != "null">
</#function>