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
package ${package}.gamerule;

import ${package}.${JavaModName};

@${JavaModName}Elements.ModElement.Tag
public class ${name}Gamerule extends ${JavaModName}Elements.ModElement {

	<#if data.type == "Number">
	public static final GameRules.RuleKey<GameRules.IntegerValue> gamerule = GameRules.register("${data.name}", GameRules.Category.${data.category}, create(0));
	<#else>
	public static final GameRules.RuleKey<GameRules.BooleanValue> gamerule = GameRules.register("${data.name}", GameRules.Category.${data.category}, create(false));
	</#if>

	public ${name}Gamerule (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	<#if data.type == "Number">
		public static GameRules.RuleType<GameRules.IntegerValue> create(int defaultValue) {
			Method method;
			try {
				method = ObfuscationReflectionHelper.findMethod(GameRules.class, "func_223564_a", GameRules.class);
				method.setAccessible(true);
				return (GameRules.RuleType<GameRules.IntegerValue>) method.invoke(null, defaultValue);
			} catch (Exception ignored) {
			}
			return null;
		}
	<#else>
		public static GameRules.RuleType<GameRules.BooleanValue> create(boolean defaultValue) {
			Method method;
			try {
				method = ObfuscationReflectionHelper.findMethod(GameRules.class, "func_223568_b", GameRules.class);
				method.setAccessible(true);
				return (GameRules.RuleType<GameRules.BooleanValue>) method.invoke(null, defaultValue);
			} catch (Exception ignored) {
			}
			return null;
		}
	</#if>
}
<#-- @formatter:on -->