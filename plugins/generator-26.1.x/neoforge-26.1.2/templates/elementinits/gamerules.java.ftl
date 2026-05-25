<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2026, Pylo, opensource contributors
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

/*
 *	MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#assign hasLogicRules = false>
<#assign hasNumberRules = false>

public class ${JavaModName}GameRules {

	public static final DeferredRegister<GameRule<?>> REGISTRY = DeferredRegister.create(Registries.GAME_RULE, ${JavaModName}.MODID);

	<#list gamerules as gamerule>
		<#if gamerule.type == "Number">
		<#assign hasNumberRules = true>
		public static DeferredHolder<GameRule<?>, GameRule<Integer>> ${gamerule.getModElement().getRegistryNameUpper()} = registerInteger("${StringUtils.lowercaseFirstLetter(gamerule.getModElement().getName())}", GameRuleCategory.${gamerule.category}, ${gamerule.defaultValueNumber});
		<#else>
		<#assign hasLogicRules = true>
		public static DeferredHolder<GameRule<?>, GameRule<Boolean>> ${gamerule.getModElement().getRegistryNameUpper()} = registerBoolean("${StringUtils.lowercaseFirstLetter(gamerule.getModElement().getName())}", GameRuleCategory.${gamerule.category}, ${gamerule.defaultValueLogic});
		</#if>
	</#list>

	<#if hasLogicRules>
	private static DeferredHolder<GameRule<?>, GameRule<Boolean>> registerBoolean(String registryname, GameRuleCategory category, boolean value) {
		return REGISTRY.register(registryname, () -> new GameRule<>(category, GameRuleType.BOOL, BoolArgumentType.bool(), GameRuleTypeVisitor::visitBoolean,
				Codec.BOOL, b -> b ? 1 : 0, value, FeatureFlagSet.of()));
	}
	</#if>

	<#if hasNumberRules>
	private static DeferredHolder<GameRule<?>, GameRule<Integer>> registerInteger(String registryname, GameRuleCategory category, int value) {
		return REGISTRY.register(registryname, () -> new GameRule<>(category, GameRuleType.INT, IntegerArgumentType.integer(Integer.MIN_VALUE, Integer.MAX_VALUE),
				GameRuleTypeVisitor::visitInteger, Codec.intRange(Integer.MIN_VALUE, Integer.MAX_VALUE), i -> i, value, FeatureFlagSet.of()));
	}
	</#if>

}
<#-- @formatter:on -->