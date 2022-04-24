(new Object(){
	public String getText(){
		String param=(String)command.get("${field$paramid}");
		if(param!=null){
		    return param;
		}
	    return"";
	}
}.getText())