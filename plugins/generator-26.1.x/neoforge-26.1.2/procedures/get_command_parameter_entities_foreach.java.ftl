try {
	for (Entity entityiterator : EntityArgument.getEntities(arguments, "${field$param}")) {
		${statement$foreach}
	}
} catch (CommandSyntaxException e) {
	e.printStackTrace();
}