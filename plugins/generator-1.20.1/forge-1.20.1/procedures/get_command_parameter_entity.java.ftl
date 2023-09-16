(new Object() {
	public Entity getEntity() {
		try {
			return EntityArgument.getEntity(arguments, "${field$param}");
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
}.getEntity())