if(!entity.world.isRemote && entity.world.getMinecraftServer() != null) {
	FunctionObject _fobj = entity.world.getMinecraftServer().getFunctionManager().getFunction(new ResourceLocation(${input$function}));
	if(_fobj != null)  {
		world.getMinecraftServer().getFunctionManager().execute(_fobj, new ICommandSender() {

    	@Override public String getName() {
		return "";
		}

		@Override public boolean canUseCommand(int permission, String command) {
		return true;
		}

        @Override public World getEntityWorld() {
		return entity.world;
		}

        @Override public MinecraftServer getServer() {
		return entity.world.getMinecraftServer();
		}

		@Override public boolean sendCommandFeedback() {
		return false;
		}

		@Override public BlockPos getPosition(){
		return entity.getPosition();
		}

        @Override public Vec3d getPositionVector() {
		return new Vec3d(entity.posX, entity.posY, entity.posZ);
		}

		@Override public Entity getCommandSenderEntity() {
		return entity;
		}

		});
	}
}