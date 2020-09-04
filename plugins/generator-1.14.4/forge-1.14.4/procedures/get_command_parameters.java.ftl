(new Object(){
	public String getText(){
		String param=(String)cmdparams.get("${field$paramid}");
		if(param!=null){
		return param;
		}
		return"";
		}
		}.getText())