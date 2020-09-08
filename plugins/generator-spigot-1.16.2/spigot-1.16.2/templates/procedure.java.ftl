<#-- @formatter:off -->
package ${package}.procedures;

import org.bukkit.entity.Entity;

public class ${name}Procedure implements org.bukkit.event.Listener {

	public static <#if return_type??>${return_type.getJavaType(generator.getWorkspace())}<#else>void</#if> executeProcedure(Map<String, Object> dependencies){
		<#list dependencies as dependency>
		if(dependencies.get("${dependency.getName()}")==null){
			System.err.println("Failed to load dependency ${dependency.getName()} for procedure ${name}!");
			<#if return_type??>return ${return_type.getDefaultValue(generator.getWorkspace())}<#else>return</#if>;
		}
        </#list>

		<#list dependencies as dependency>
			<#if dependency.getType(generator.getWorkspace()) == "double">
				double ${dependency.getName()} = dependencies.get("${dependency.getName()}") instanceof Integer
					? (int) dependencies.get("${dependency.getName()}") : (double) dependencies.get("${dependency.getName()}");
			<#elseif dependency.getType(generator.getWorkspace()) == "Entity">
			${dependency.getType(generator.getWorkspace())} ${dependency.getName()} = (${dependency.getType(generator.getWorkspace())}) dependencies.get("${dependency.getName()}");
			<#else>
            	${dependency.getType(generator.getWorkspace())} ${dependency.getName()} = (${dependency.getType(generator.getWorkspace())}) dependencies.get("${dependency.getName()}");
			</#if>
        </#list>

		${procedurecode}

	}

	${trigger_code}

}
<#-- @formatter:on -->
