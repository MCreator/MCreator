try {
    for (Entity entityiterator : EntityArgument.getEntitiesAllowingNone(arguments, "${field$param}")) {
        ${statement$foreach}
    }
} catch (CommandSyntaxException e) {
	e.printStackTrace();
}