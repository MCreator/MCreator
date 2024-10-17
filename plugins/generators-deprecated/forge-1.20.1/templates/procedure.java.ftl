<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2023, Pylo, opensource contributors
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

import net.minecraftforge.eventbus.api.Event;

<#assign nullableDependencies = []/>
<#list dependencies as dependency>
	<#if dependency.getType(generator.getWorkspace()) != "double"
		&& dependency.getType(generator.getWorkspace()) != "LevelAccessor"
		&& dependency.getType(generator.getWorkspace()) != "ItemStack"
		&& dependency.getType(generator.getWorkspace()) != "BlockState"
		&& dependency.getType(generator.getWorkspace()) != "InteractionResult"
		&& dependency.getType(generator.getWorkspace()) != "boolean"
		&& dependency.getType(generator.getWorkspace()) != "CommandContext<CommandSourceStack>">
		<#assign nullableDependencies += [dependency.getName()]/>
	</#if>
</#list>

<#compress>

<#if trigger_code?has_content>
${trigger_code}
<#else>
public class ${name}Procedure {
</#if>

	<#if trigger_code?has_content>
	public static <#if return_type??>${return_type.getJavaType(generator.getWorkspace())}<#else>void</#if> execute(
		<#list dependencies as dependency>
			${dependency.getType(generator.getWorkspace())} ${dependency.getName()}<#if dependency?has_next>,</#if>
		</#list>
	) {
		<#if return_type??>return </#if>execute(null<#if dependencies?has_content>,</#if><#list dependencies as dependency>${dependency.getName()}<#if dependency?has_next>,</#if></#list>);
	}
	</#if>

	<#if trigger_code?has_content>private <#else>public </#if>static <#if return_type??>${return_type.getJavaType(generator.getWorkspace())}<#else>void</#if> execute(
		<#if trigger_code?has_content>@Nullable Event event<#if dependencies?has_content>,</#if></#if>
		<#list dependencies as dependency>
				${dependency.getType(generator.getWorkspace())} ${dependency.getName()}<#if dependency?has_next>,</#if>
		</#list>
	) {
		<#if nullableDependencies?has_content>
			if(
			<#list nullableDependencies as dependency>
			${dependency} == null <#if dependency?has_next>||</#if>
			</#list>
			) return <#if return_type??>${return_type.getDefaultValue(generator.getWorkspace())}</#if>;
		</#if>

		<#list localvariables as var>
			<@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/>
		</#list>

		${procedurecode}
	}

}

</#compress>

<#-- @formatter:on -->
