try {
	EntityArgument.getEntitiesAllowingNone(arguments, "${field$param}").forEach(entityiterator -> {
		${statement$foreach}
	});
} catch (CommandSyntaxException e) {
	e.printStackTrace();
}