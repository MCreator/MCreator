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

<#include "../mcitems.ftl">
<#include "../procedures.java.ftl">

/*
 *	MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

@EventBusSubscriber public class ${JavaModName}Fuels {

	@SubscribeEvent public static void modifyItemComponents(ModifyDefaultComponentsEvent event) {
		<@javacompress>
		<#list itemextensions?filter(e -> e.enableFuel) as extension>
		event.modify(${mappedMCItemToItem(extension.item)}, (builder, _, item) -> {
			builder.set(DataComponents.COOKING_FUEL, new CookingFuel(
			<#if hasProcedure(extension.fuelPower) && (hasProcedure(extension.fuelPower) || hasProcedure(extension.fuelSuccessCondition))>
				ResolvableInt.fromKey(ResourceKey.create(Registries.CONTEXT_INT_PROVIDER,
					Identifier.fromNamespaceAndPath("${modid}", "fuel/${extension.getModElement().getRegistryName()}"))),
				<#else>
				new ResolvableInt.Constant(${extension.fuelPower.getFixedValue()}),
				</#if>
				ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER)));
		});
		</#list>
		</@javacompress>
	}

	<#if itemextensions?filter(e -> hasProcedure(e.fuelPower))?size != 0>
	@SubscribeEvent public static void registerContextIntProvider(RegisterEvent event) {
		event.register(Registries.CONTEXT_INT_PROVIDER_TYPE, Identifier.fromNamespaceAndPath("${modid}", "procedure_fuel"), () -> ProcedureFuelProvider.CODEC);
	}

	public static final class ProcedureFuelProvider implements ContextIntProvider {
		private final String itemExtension;

		public static final MapCodec<ProcedureFuelProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.STRING.fieldOf("item_extension").forGetter(provider -> provider.itemExtension)).apply(instance, ProcedureFuelProvider::new));

		public ProcedureFuelProvider(String item) {
			this.itemExtension = item;
		}

		@Override public int getIntUnsafe(LootContext context) {
			ItemStack itemstack = (ItemStack) context.getOptional(NeoForgeLootContextParams.QUERIED_STACK);
			<@javacompress>
			<#list itemextensions?filter(e -> hasProcedure(e.fuelPower)) as extension>
			if (itemExtension.equals("${extension.getModElement().getRegistryName()}")) {
				<#if hasProcedure(extension.fuelSuccessCondition)>
				if (!(<@procedureToRetvalCode name=extension.fuelSuccessCondition.getName() dependencies=extension.fuelSuccessCondition.getDependencies(generator.getWorkspace()) customVals={"x":"context.getOptional(LootContextParams.ORIGIN).x()", "y":"context.getOptional(LootContextParams.ORIGIN).y()", "z":"context.getOptional(LootContextParams.ORIGIN).z()", "itemstack":"itemstack"}/>))
					return 0;
				</#if>
				return (int) <#if hasProcedure(extension.fuelPower)><@procedureToRetvalCode name=extension.fuelPower.getName() dependencies=extension.fuelPower.getDependencies(generator.getWorkspace()) customVals={"x":"context.getOptional(LootContextParams.ORIGIN).x()", "y":"context.getOptional(LootContextParams.ORIGIN).y()", "z":"context.getOptional(LootContextParams.ORIGIN).z()", "itemstack":"itemstack"}/><#else>${extension.fuelPower.getFixedValue()}</#if>;
			}
			</#list>
			</@javacompress>
			return 0;
		}

		@Override public MapCodec<ProcedureFuelProvider> codec() {
			return CODEC;
		}

		@Override public void validate(ValidationContext context) {
		}
	}
	</#if>
}
<#-- @formatter:on -->