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

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#assign blockentitiesWithInventory = w.getGElementsOfType("block")?filter(e -> e.hasInventory)>

<#if blockentitiesWithInventory?size != 0>
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
</#if>
public class ${JavaModName}BlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ${JavaModName}.MODID);

	<#list blockentities as blockentity>
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> ${blockentity.getModElement().getRegistryNameUpper()} =
		register("${blockentity.getModElement().getRegistryName()}", ${JavaModName}Blocks.${blockentity.getModElement().getRegistryNameUpper()},
			${blockentity.getModElement().getName()}BlockEntity::new);
	</#list>

	private static DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(String registryname, DeferredHolder<Block, Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}

	<#if blockentitiesWithInventory?size != 0>
	<#compress>
	@SubscribeEvent public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		<#list blockentitiesWithInventory as blockentity>
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ${blockentity.getModElement().getRegistryNameUpper()}.get(),
				(blockEntity, side) -> ((${blockentity.getModElement().getName()}BlockEntity) blockEntity).getItemHandler());
			<#if blockentity.hasEnergyStorage>
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ${blockentity.getModElement().getRegistryNameUpper()}.get(),
				(blockEntity, side) -> ((${blockentity.getModElement().getName()}BlockEntity) blockEntity).getEnergyStorage());
			</#if>
			<#if blockentity.isFluidTank>
			event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ${blockentity.getModElement().getRegistryNameUpper()}.get(),
				(blockEntity, side) -> ((${blockentity.getModElement().getName()}BlockEntity) blockEntity).getFluidTank());
			</#if>
		</#list>
	}
	</#compress>
	</#if>

}
<#-- @formatter:on -->