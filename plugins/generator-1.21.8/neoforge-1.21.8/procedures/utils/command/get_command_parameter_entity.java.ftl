private static Entity commandParameterEntity(CommandContext<CommandSourceStack> arguments, String parameter) {
	try {
		return EntityArgument.getEntity(arguments, parameter);
	} catch (CommandSyntaxException e) {
		e.printStackTrace();
		return null;
	}
}