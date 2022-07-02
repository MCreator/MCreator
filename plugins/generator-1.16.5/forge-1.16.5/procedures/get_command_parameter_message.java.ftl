(new Object() {
    public String getMessage() {
	    try {
		    return MessageArgument.getMessage(arguments, "message").getString();
	    } catch (CommandSyntaxException ignored) {
			return "";
		}
	}
}).getMessage()