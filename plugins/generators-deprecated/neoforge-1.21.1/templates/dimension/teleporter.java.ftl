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
<#include "../mcitems.ftl">

package ${package}.world.teleporter;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD) public class ${name}Teleporter {

	public static Holder<PoiType> poi = null;

	@SubscribeEvent public static void registerPointOfInterest(RegisterEvent event) {
		event.register(Registries.POINT_OF_INTEREST_TYPE, registerHelper -> {
			PoiType poiType = new PoiType(ImmutableSet.copyOf(${JavaModName}Blocks.${registryname?upper_case}_PORTAL.get().getStateDefinition().getPossibleStates()), 0, 1);
			registerHelper.register(ResourceLocation.parse("${modid}:${registryname}_portal"), poiType);
			poi = BuiltInRegistries.POINT_OF_INTEREST_TYPE.wrapAsHolder(poiType);
		});
	}

	private final ServerLevel level;

	public ${name}Teleporter(ServerLevel level) {
		this.level = level;
	}

	${mcc.getMethod("net.minecraft.world.level.portal.PortalForcer", "findClosestPortalPosition", "BlockPos", "boolean", "WorldBorder")
		 .replace("PoiTypes.NETHER_PORTAL", "poi.unwrapKey().get()")}

	${mcc.getMethod("net.minecraft.world.level.portal.PortalForcer", "createPortal", "BlockPos", "Direction.Axis")
		 .replace("Blocks.OBSIDIAN", mappedBlockToBlock(data.portalFrame)?string)
		 .replace(",blockstate,18);", ", blockstate, 18);\nthis.level.getPoiManager().add(blockpos$mutableblockpos, poi);")
		 .replace("Blocks.NETHER_PORTAL", JavaModName + "Blocks." + registryname?upper_case + "_PORTAL.get()")}

	${mcc.getMethod("net.minecraft.world.level.portal.PortalForcer", "canHostFrame", "BlockPos", "BlockPos.MutableBlockPos", "Direction", "int")}

	private boolean canPortalReplaceBlock(BlockPos.MutableBlockPos pos) {
		BlockState blockstate = this.level.getBlockState(pos);
		return blockstate.canBeReplaced() && blockstate.getFluidState().isEmpty();
	}

}

<#-- @formatter:on -->