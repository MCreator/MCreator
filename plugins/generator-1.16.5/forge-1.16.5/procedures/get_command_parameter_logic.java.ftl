(new Object(){
	public boolean getValue(){
		boolean param=(boolean)cmdparams.get("${field$paramid}");
		if(cmdparams.get("${field$paramid}")!=null){
		return param;
		}
		return false;
		}
		}.getValue())