private static ItemStack commandParameterItemStack(CommandContext<CommandSourceStack> arguments, String parameter) {
	ItemInput input = ItemArgument.getItem(arguments, parameter);
	try {
		return input.createItemStack(1, false);
	} catch (CommandSyntaxException e) {
		e.printStackTrace();
		return input.getItem().getDefaultInstance();
	}
}