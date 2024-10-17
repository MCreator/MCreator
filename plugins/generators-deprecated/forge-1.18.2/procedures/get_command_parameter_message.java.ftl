(new Object() {
    public String getMessage() {
	    try {
		    return MessageArgument.getMessage(arguments, "${field$param}").getString();
	    } catch (CommandSyntaxException ignored) {
			return "";
		}
	}
}).getMessage()