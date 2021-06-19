(new Object(){
	public boolean getValue(){
		CheckboxButton checkbox=(CheckboxButton)guistate.get("checkbox:${field$checkbox}");
		if(checkbox!=null){
			return checkbox.isChecked();
		}
		return false;
	}
}.getValue())