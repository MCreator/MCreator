if(!world.isRemote && world.getMinecraftServer() != null) {
		world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {

    	@Override public String getName() {
    		return "";
        }

		@Override public boolean canUseCommand(int permission, String command) {
    		return true;
        }

        @Override public World getEntityWorld() {
    		return world;
        }

        @Override public MinecraftServer getServer() {
    		return world.getMinecraftServer();
        }

		@Override public boolean sendCommandFeedback() {
		    return false;
		}

		@Override public BlockPos getPosition(){
		    return new BlockPos((int)${input$x}, (int)${input$y}, (int)${input$z});
		}

        @Override public Vec3d getPositionVector() {
		    return new Vec3d(${input$x}, ${input$y}, ${input$z});
		}

    }, ${input$command});
}