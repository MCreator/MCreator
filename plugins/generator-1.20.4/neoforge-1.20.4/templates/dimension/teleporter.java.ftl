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

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${name}Teleporter implements ITeleporter {

	public static final TicketType<BlockPos> CUSTOM_PORTAL = TicketType.create("${registryname}_portal", Vec3i::compareTo, 300);

	public static Holder<PoiType> poi = null;

	@SubscribeEvent public static void registerPointOfInterest(RegisterEvent event) {
		event.register(Registries.POINT_OF_INTEREST_TYPE, registerHelper -> {
			PoiType poiType = new PoiType(ImmutableSet.copyOf(${JavaModName}Blocks.${registryname?upper_case}_PORTAL.get().getStateDefinition().getPossibleStates()), 0, 1);
			registerHelper.register(new ResourceLocation("${modid}:${registryname}_portal"), poiType);
			poi = BuiltInRegistries.POINT_OF_INTEREST_TYPE.wrapAsHolder(poiType);
		});
	}

	private final ServerLevel level;
	private final BlockPos entityEnterPos;

	public ${name}Teleporter(ServerLevel worldServer, BlockPos entityEnterPos) {
		this.level = worldServer;
		this.entityEnterPos = entityEnterPos;
	}

	${mcc.getMethod("net.minecraft.world.level.portal.PortalForcer", "findPortalAround", "BlockPos", "boolean", "WorldBorder")
		.replace("PoiTypes.NETHER_PORTAL", "poi.unwrapKey().get()")
		.replace("TicketType.PORTAL", "CUSTOM_PORTAL")
		.replace("Blocks.NETHER_PORTAL", JavaModName + "Blocks." + registryname?upper_case + "_PORTAL.get()")}

	${mcc.getMethod("net.minecraft.world.level.portal.PortalForcer", "createPortal", "BlockPos", "Direction.Axis")
		.replace("Blocks.OBSIDIAN", mappedBlockToBlock(data.portalFrame)?string)
		.replace(",blockstate,18);", ", blockstate, 18);\nthis.level.getPoiManager().add(blockpos$mutableblockpos, poi);")
		.replace("Blocks.NETHER_PORTAL", JavaModName + "Blocks." + registryname?upper_case + "_PORTAL.get()")}

	${mcc.getMethod("net.minecraft.world.level.portal.PortalForcer", "canHostFrame", "BlockPos", "BlockPos.MutableBlockPos", "Direction", "int")}

	@Override
	public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel server, float yaw,
			Function<Boolean, Entity> repositionEntity) {
		PortalInfo portalinfo = getPortalInfo(entity, server);

		if (entity instanceof ServerPlayer player) {
			player.setServerLevel(server);
			server.addDuringPortalTeleport(player);

			player.connection.teleport(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z, portalinfo.yRot, portalinfo.xRot);
			player.connection.resetPosition();

			CriteriaTriggers.CHANGED_DIMENSION.trigger(player, currentWorld.dimension(), server.dimension());

			return entity;
		} else {
			Entity entityNew = entity.getType().create(server);
			if (entityNew != null) {
				entityNew.restoreFrom(entity);
				entityNew.moveTo(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z, portalinfo.yRot, entityNew.getXRot());
				entityNew.setDeltaMovement(portalinfo.speed);
				server.addDuringTeleport(entityNew);
			}
			return entityNew;
		}
	}

	private PortalInfo getPortalInfo(Entity entity, ServerLevel server) {
		WorldBorder worldborder = server.getWorldBorder();
		double d0 = DimensionType.getTeleportationScale(entity.level().dimensionType(), server.dimensionType());
		BlockPos blockpos1 = worldborder.clampToBounds(entity.getX() * d0, entity.getY(), entity.getZ() * d0);
		return this.getExitPortal(entity, blockpos1, worldborder).map(repositioner -> {
			BlockState blockstate = entity.level().getBlockState(this.entityEnterPos);
			Direction.Axis direction$axis;
			Vec3 vector3d;

			if (blockstate.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
				direction$axis = blockstate.getValue(BlockStateProperties.HORIZONTAL_AXIS);
				BlockUtil.FoundRectangle teleportationrepositioner$result = BlockUtil.getLargestRectangleAround(this.entityEnterPos, direction$axis, 21, Direction.Axis.Y, 21,
								pos -> entity.level().getBlockState(pos) == blockstate);
				vector3d = ${name}PortalShape.getRelativePosition(teleportationrepositioner$result, direction$axis, entity.position(), entity.getDimensions(entity.getPose()));
			} else {
				direction$axis = Direction.Axis.X;
				vector3d = new Vec3(0.5, 0, 0);
			}

			return ${name}PortalShape.createPortalInfo(server, repositioner, direction$axis, vector3d, entity, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
		}).orElse(new PortalInfo(entity.position(), Vec3.ZERO, entity.getYRot(), entity.getXRot()));
	}

	protected Optional<BlockUtil.FoundRectangle> getExitPortal(Entity entity, BlockPos pos, WorldBorder worldBorder) {
		Optional<BlockUtil.FoundRectangle> optional = this.findPortalAround(pos, false, worldBorder);

		if (entity instanceof ServerPlayer) {
			if (optional.isPresent()) {
				return optional;
			} else {
				Direction.Axis direction$axis = entity.level().getBlockState(this.entityEnterPos).getOptionalValue(NetherPortalBlock.AXIS).orElse(Direction.Axis.X);
				return this.createPortal(pos, direction$axis);
			}
		} else {
			return optional;
		}
	}

	private boolean canPortalReplaceBlock(BlockPos.MutableBlockPos pos) {
		BlockState blockstate = this.level.getBlockState(pos);
		return blockstate.canBeReplaced() && blockstate.getFluidState().isEmpty();
	}

}

<#-- @formatter:on -->