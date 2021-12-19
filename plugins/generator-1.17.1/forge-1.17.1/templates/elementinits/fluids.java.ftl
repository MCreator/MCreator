<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2021, Pylo, opensource contributors
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
 * MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${JavaModName}Fluids {

	private static final List<Fluid> REGISTRY = new ArrayList<>();

	<#list fluids as fluid>
	public static final FlowingFluid ${fluid.getModElement().getRegistryNameUpper()} = register(new ${fluid.getModElement().getName()}Fluid.Source());
	public static final FlowingFluid FLOWING_${fluid.getModElement().getRegistryNameUpper()} = register(new ${fluid.getModElement().getName()}Fluid.Flowing());
	</#list>

	private static FlowingFluid register(FlowingFluid fluid) {
		REGISTRY.add(fluid);
		return fluid;
	}

	@SubscribeEvent public static void registerFluids(RegistryEvent.Register<Fluid> event) {
		event.getRegistry().registerAll(REGISTRY.toArray(new Fluid[0]));
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT) public static class ClientSideHandler {
	    @SubscribeEvent public static void clientSetup(FMLClientSetupEvent event) {
	    	<#list fluids as fluid>
			ItemBlockRenderTypes.setRenderLayer(${fluid.getModElement().getRegistryNameUpper()}, renderType -> renderType == RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(FLOWING_${fluid.getModElement().getRegistryNameUpper()}, renderType -> renderType == RenderType.translucent());
            </#list>
		}
	}
}

<#-- @formatter:on -->