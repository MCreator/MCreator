public static class TeleporterDimensionMod extends Teleporter {

	private Vec3d lastPortalVec;
	private EnumFacing teleportDirection;

	public TeleporterDimensionMod(WorldServer worldServer, Vec3d lastPortalVec, EnumFacing teleportDirection) {
		super(worldServer);
		this.lastPortalVec = lastPortalVec;
		this.teleportDirection = teleportDirection;
	}

	@Override ${mcc.getMethod("net.minecraft.world.Teleporter", "makePortal", "Entity")
				   .replace("Blocks.OBSIDIAN", mappedBlockToBlockStateCode(data.portalFrame) + ".getBlock()")
				   .replace("Blocks.PORTAL", "portal")}

	@Override ${mcc.getMethod("net.minecraft.world.Teleporter", "placeInPortal", "Entity", "float")
				   .replace("Blocks.OBSIDIAN", mappedBlockToBlockStateCode(data.portalFrame) + ".getBlock()")}

	@Override ${mcc.getMethod("net.minecraft.world.Teleporter", "placeInExistingPortal", "Entity", "float")
				   .replace("entityIn.getTeleportDirection()", "teleportDirection")
				   .replace("entityIn.getLastPortalVec()", "lastPortalVec")
				   .replace("Blocks.PORTAL", "portal")}

}