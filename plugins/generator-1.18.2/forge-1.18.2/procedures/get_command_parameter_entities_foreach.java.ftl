try {
	EntityArgument.getEntities(cmdargs, "${field$param}").forEach(entityiterator -> {
		${statement$foreach}
	});
} catch (CommandSyntaxException e) {
	e.printStackTrace();
}