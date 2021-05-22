<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
 # 
 # This program is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 3 of the License, or
 # (at your option) any later version.
 # 
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 # 
 # You should have received a copy of the GNU General Public License
 # along with this program.  If not, see <https://www.gnu.org/licenses/>.
 # 
 # Additional permission for code generator templates (*.ftl files)
 # 
 # As a special exception, you may create a larger work that contains part or 
 # all of the MCreator code generator templates (*.ftl files) and distribute 
 # that work under terms of your choice, so long as that work isn't itself a 
 # template for code generation. Alternatively, if you modify or redistribute 
 # the template itself, you may (at your option) remove this special exception, 
 # which will cause the template and the resulting code generator output files 
 # to be licensed under the GNU General Public License without this special 
 # exception.
-->

<#-- @formatter:off -->
package ${package}.procedures;

public class ${name}Procedure {

	${trigger_code}

	public static <#if return_type??>${return_type.getJavaType(generator.getWorkspace())}<#else>void</#if> executeProcedure(Map<String, Object> dependencies){
		<#list dependencies as dependency>
		if(dependencies.get("${dependency.getName()}") == null) {
			if(!dependencies.containsKey("${dependency.getName()}"))
				${JavaModName}.LOGGER.warn("Failed to load dependency ${dependency.getName()} for procedure ${name}!");
			<#if return_type??>return ${return_type.getDefaultValue(generator.getWorkspace())}<#else>return</#if>;
		}
        </#list>

		<#list dependencies as dependency>
			<#if dependency.getType(generator.getWorkspace()) == "double">
				double ${dependency.getName()} = dependencies.get("${dependency.getName()}") instanceof Integer
					? (int) dependencies.get("${dependency.getName()}") : (double) dependencies.get("${dependency.getName()}");
			<#else>
				${dependency.getType(generator.getWorkspace())} ${dependency.getName()} = (${dependency.getType(generator.getWorkspace())}) dependencies.get("${dependency.getName()}");
			</#if>
		</#list>

		${procedurecode}

	}

}
<#-- @formatter:on -->
