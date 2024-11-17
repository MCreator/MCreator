<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2024, Pylo, opensource contributors
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
package ${package}.block.grower;

<#compress>
public class ${name}TreeGrower extends <#if (data.megaTrees[0]?has_content) || (data.megaTrees[1]?has_content)>AbstractMegaTreeGrower<#else>AbstractTreeGrower</#if> {
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean hasFlower) {
		<#if data.secondaryTreeChance != 0 && (data.trees[1]?has_content || data.flowerTrees[1]?has_content)>
		if (randomSource.nextFloat() < ${data.secondaryTreeChance}) {
			<#if data.flowerTrees[1]?has_content>
			if (hasFlower)
				return <@toTreeKey data.flowerTrees[1]/>;
			</#if>
			<#if data.trees[1]?has_content>
			return <@toTreeKey data.trees[1]/>;
			</#if>
		}
		</#if>
		return <#if data.flowerTrees[0]?has_content>hasFlower ? <@toTreeKey data.flowerTrees[0]/> :</#if> <@toTreeKey data.trees[0]/>;
	}

	<#if (data.megaTrees[0]?has_content) || (data.megaTrees[1]?has_content)>
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource randomSource) {
		<#if data.megaTrees[1]?has_content && data.secondaryTreeChance != 0>
		return (randomSource.nextFloat() < ${data.secondaryTreeChance}) ? <@toTreeKey data.megaTrees[1]/> : <@toTreeKey data.megaTrees[0]/>;
		<#else>
		return <@toTreeKey data.megaTrees[0]/>;
		</#if>
	}
	</#if>
}
</#compress>
<#-- @formatter:on -->

<#macro toTreeKey tree="">
<#if tree?has_content>
FeatureUtils.createKey("${generator.map(tree, "configuredfeatures")}")
<#else>
null
</#if>
</#macro>