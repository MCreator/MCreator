try {
	EntityArgument.getEntitiesAllowingNone(cmdargs, "${field$param}").forEach(entityiterator -> {
		${statement$foreach}
	});
} catch (CommandSyntaxException e) {
	e.printStackTrace();
}