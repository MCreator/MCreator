private static String commandParameterMessage(CommandContext<CommandSourceStack> arguments, String parameter) {
	try {
		return MessageArgument.getMessage(arguments, parameter).getString();
	} catch (CommandSyntaxException e) {
		e.printStackTrace();
		return "";
	}
}