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
public static class TeleporterDimensionMod implements ITeleporter {

	private final ServerWorld world;
	private final BlockPos entityEnterPos;

	public TeleporterDimensionMod(ServerWorld worldServer, BlockPos entityEnterPos) {
		this.world = worldServer;
		this.entityEnterPos = entityEnterPos;
	}

	${mcc.getMethod("net.minecraft.world.Teleporter", "getExistingPortal", "BlockPos", "boolean")
		.replace("PointOfInterestType.NETHER_PORTAL", "poi")
		.replace("TicketType.PORTAL", "CUSTOM_PORTAL")
		.replace("Blocks.NETHER_PORTAL", "portal")}

	${mcc.getMethod("net.minecraft.world.Teleporter", "makePortal", "BlockPos", "Direction.Axis")
		.replace("Blocks.OBSIDIAN", mappedBlockToBlockStateCode(data.portalFrame) + ".getBlock()")
		.replace(",blockstate,18);", ", blockstate, 18);\nthis.world.getPointOfInterestManager().add(blockpos$mutable, poi);")
		.replace("Blocks.NETHER_PORTAL", "portal")}

	${mcc.getMethod("net.minecraft.world.Teleporter", "checkRegionForPlacement", "BlockPos", "BlockPos.Mutable", "Direction", "int")}

	@Override
	public Entity placeEntity(Entity entity, ServerWorld serverworld, ServerWorld server, float yaw,
			Function<Boolean, Entity> repositionEntity) {
		PortalInfo portalinfo = getPortalInfo(entity, server);

		if (entity instanceof ServerPlayerEntity) {
			entity.setWorld(server);
			server.addDuringPortalTeleport((ServerPlayerEntity) entity);

			entity.rotationYaw = portalinfo.rotationYaw % 360.0F;
			entity.rotationPitch = portalinfo.rotationPitch % 360.0F;

			entity.moveForced(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z);

			return entity;
		} else {
			Entity entityNew = entity.getType().create(server);
			if (entityNew != null) {
				entityNew.copyDataFromOld(entity);
				entityNew.setLocationAndAngles(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z,
						portalinfo.rotationYaw, entityNew.rotationPitch);
				entityNew.setMotion(portalinfo.motion);
				server.addFromAnotherDimension(entityNew);
			}
			return entityNew;
		}
	}

	private PortalInfo getPortalInfo(Entity entity, ServerWorld server) {
		WorldBorder worldborder = entity.world.getWorldBorder();
		double d0 = Math.max(-2.9999872E7D, worldborder.minX() + 16);
		double d1 = Math.max(-2.9999872E7D, worldborder.minZ() + 16);
		double d2 = Math.min(2.9999872E7D, worldborder.maxX() - 16);
		double d3 = Math.min(2.9999872E7D, worldborder.maxZ() - 16);
		double d4 = DimensionType.getCoordinateDifference(entity.world.getDimensionType(), server.getDimensionType());
		BlockPos blockpos1 = new BlockPos(MathHelper.clamp(entity.getPosX() * d4, d0, d2), entity.getPosY(),
				MathHelper.clamp(entity.getPosZ() * d4, d1, d3));
		return this.getPortalRepositioner(entity, server, blockpos1).map(repositioner -> {
			BlockState blockstate = entity.world.getBlockState(this.entityEnterPos);
			Direction.Axis direction$axis;
			Vector3d vector3d;

			if (blockstate.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
				direction$axis = blockstate.get(BlockStateProperties.HORIZONTAL_AXIS);
				TeleportationRepositioner.Result teleportationrepositioner$result = TeleportationRepositioner
						.findLargestRectangle(this.entityEnterPos, direction$axis, 21, Direction.Axis.Y, 21,
								pos -> entity.world.getBlockState(pos) == blockstate);
				vector3d = CustomPortalSize.func_242973_a(teleportationrepositioner$result, direction$axis, entity.getPositionVec(), entity.getSize(entity.getPose()));
			} else {
				direction$axis = Direction.Axis.X;
				vector3d = new Vector3d(0.5, 0, 0);
			}

			return CustomPortalSize.func_242963_a(server, repositioner, direction$axis, vector3d, entity.getSize(entity.getPose()),
							entity.getMotion(), entity.rotationYaw, entity.rotationPitch);
		}).orElse(new PortalInfo(entity.getPositionVec(), Vector3d.ZERO, entity.rotationYaw, entity.rotationPitch));
	}

	protected Optional<TeleportationRepositioner.Result> getPortalRepositioner(Entity entity, ServerWorld server, BlockPos pos) {
		Optional<TeleportationRepositioner.Result> optional = this.getExistingPortal(pos, false);

		if (entity instanceof ServerPlayerEntity) {
			if (optional.isPresent()) {
				return optional;
			} else {
				Direction.Axis direction$axis = this.world.getBlockState(this.entityEnterPos).func_235903_d_(NetherPortalBlock.AXIS).orElse(Direction.Axis.X);
				return this.makePortal(pos, direction$axis);
			}
		} else {
			return optional;
		}
	}

}