(new Object() {
	public double getX() {
		try {
			return BlockPosArgument.getLoadedBlockPos(arguments, "${field$param}").getX();
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
			return 0;
		}
	}
}.getX())