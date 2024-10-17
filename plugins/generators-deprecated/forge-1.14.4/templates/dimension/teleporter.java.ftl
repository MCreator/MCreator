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

public static class TeleporterDimensionMod extends Teleporter {

	private static final Logger LOGGER = LogManager.getLogger();

	private Vec3d lastPortalVec;
	private Direction teleportDirection;

	protected final ServerWorld world;
	protected final Random random;
	protected final Map<ColumnPos, TeleporterDimensionMod.PortalPosition> destinationCoordinateCache = Maps.newHashMapWithExpectedSize(4096);
	private final Object2LongMap<ColumnPos> field_222275_f = new Object2LongOpenHashMap();

	public TeleporterDimensionMod(ServerWorld worldServer, Vec3d lastPortalVec, Direction teleportDirection) {
		super(worldServer);

		this.world = worldServer;
		this.random = new Random(worldServer.getSeed());
		this.lastPortalVec = lastPortalVec;
		this.teleportDirection = teleportDirection;

		worldServer.customTeleporters.add(this);
	}

	@Override ${mcc.getMethod("net.minecraft.world.Teleporter", "makePortal", "Entity")
				   .replace("Blocks.OBSIDIAN", mappedBlockToBlockStateCode(data.portalFrame) + ".getBlock()")
				   .replace("BLOCK_NETHER_PORTAL", "portal")}

	@Override ${mcc.getMethod("net.minecraft.world.Teleporter", "func_222272_a", "BlockPos", "Vec3d", "Direction", "double", "double", "boolean")
				   .replace("BLOCK_NETHER_PORTAL", "portal")
				   .replace("Teleporter.PortalPosition", "TeleporterDimensionMod.PortalPosition")}

	@Override ${mcc.getMethod("net.minecraft.world.Teleporter", "func_222268_a", "Entity", "float")
				   .replace("p_222268_1_.getTeleportDirection()", "teleportDirection")
				   .replace("p_222268_1_.getLastPortalVec()", "lastPortalVec")}

	public static class PortalPosition ${mcc.getInnerClassBody("net.minecraft.world.Teleporter", "PortalPosition")}

}