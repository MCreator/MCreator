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

private static PointOfInterestType poi = null;

public static final TicketType<BlockPos> CUSTOM_PORTAL = TicketType.create("${registryname}_portal", Vec3i::compareTo, 300);

@SubscribeEvent public void registerPointOfInterest(RegistryEvent.Register<PointOfInterestType> event) {
	try {
		Method method = ObfuscationReflectionHelper.findMethod(PointOfInterestType.class, "func_226359_a_", String.class, Set.class, int.class, int.class);
		method.setAccessible(true);
		poi = (PointOfInterestType) method.invoke(null, "${registryname}_portal", Sets.newHashSet(ImmutableSet.copyOf(portal.getStateContainer().getValidStates())), 0, 1);
		event.getRegistry().register(poi);
	} catch (Exception e) {
		e.printStackTrace();
	}
}

public static class TeleporterDimensionMod implements ITeleporter {

	private Vec3d lastPortalVec;
	private Direction teleportDirection;

	protected final ServerWorld world;
	protected final Random random;

	public TeleporterDimensionMod(ServerWorld worldServer, Vec3d lastPortalVec, Direction teleportDirection) {
		this.world = worldServer;
		this.random = new Random(worldServer.getSeed());

		this.lastPortalVec = lastPortalVec;
		this.teleportDirection = teleportDirection;
	}

	${mcc.getMethod("net.minecraft.world.Teleporter", "placeInExistingPortal", "BlockPos", "Vec3d", "Direction", "double", "double", "boolean")
				   .replace("NetherPortalBlock.createPatternHelper", name + "Dimension.CustomPortalBlock.createPatternHelper")
				   .replace("PointOfInterestType.NETHER_PORTAL", "poi")
				   .replace("TicketType.PORTAL", "CUSTOM_PORTAL")
				   .replace("Blocks.NETHER_PORTAL", "portal")}

	${mcc.getMethod("net.minecraft.world.Teleporter", "placeInPortal", "Entity", "float")
				   .replace("p_222268_1_.getTeleportDirection()", "teleportDirection")
				   .replace("p_222268_1_.getLastPortalVec()", "lastPortalVec")}

	${mcc.getMethod("net.minecraft.world.Teleporter", "makePortal", "Entity")
					.replace("Blocks.OBSIDIAN", mappedBlockToBlockStateCode(data.portalFrame) + ".getBlock()")
					.replace(",blockstate,18);", ",blockstate,18);\nthis.world.getPointOfInterestManager().add(blockpos$mutable, poi);")
					.replace("Blocks.NETHER_PORTAL", "portal")}

	@Override public Entity placeEntity(Entity entity, ServerWorld serverworld, ServerWorld serverworld1, float yaw, Function<Boolean, Entity> repositionEntity) {
		double d0 = entity.getPosX();
		double d1 = entity.getPosY();
		double d2 = entity.getPosZ();

		if(entity instanceof ServerPlayerEntity) {
			entity.setLocationAndAngles(d0, d1, d2, yaw, entity.rotationPitch);

			if (!this.placeInPortal(entity, yaw)) {
				this.makePortal(entity);
				this.placeInPortal(entity, yaw);
			}

			entity.setWorld(serverworld1);
			serverworld1.addDuringPortalTeleport((ServerPlayerEntity) entity);
			((ServerPlayerEntity) entity).connection.setPlayerLocation(entity.getPosX(), entity.getPosY(), entity.getPosZ(), yaw, entity.rotationPitch);

			return entity;
		} else {
			Vec3d vec3d = entity.getMotion();
			BlockPos blockpos = new BlockPos(d0, d1, d2);

			BlockPattern.PortalInfo blockpattern$portalinfo = this.placeInExistingPortal(blockpos, vec3d, teleportDirection,
					lastPortalVec.x, lastPortalVec.y, entity instanceof PlayerEntity);
			if (blockpattern$portalinfo == null)
				return null;

			blockpos = new BlockPos(blockpattern$portalinfo.pos);
			vec3d = blockpattern$portalinfo.motion;
			float f = (float) blockpattern$portalinfo.rotation;

			Entity entityNew = entity.getType().create(serverworld1);
			if (entityNew != null) {
				entityNew.copyDataFromOld(entity);
				entityNew.moveToBlockPosAndAngles(blockpos, entityNew.rotationYaw + f, entityNew.rotationPitch);
				entityNew.setMotion(vec3d);
				serverworld1.addFromAnotherDimension(entityNew);
			}

			return entityNew;
		}
	}

}