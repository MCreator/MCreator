private static BlockPos commandParameterBlockPos(CommandContext<CommandSourceStack> arguments, String parameter) {
	try {
		return BlockPosArgument.getLoadedBlockPos(arguments, parameter);
	} catch (CommandSyntaxException e) {
		e.printStackTrace();
		return new BlockPos(0, 0, 0);
	}
}