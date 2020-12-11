(new Object(){
	public String getText(){
		TextFieldWidget textField=(TextFieldWidget)guistate.get("text:${field$textfield}");
		if(textField!=null){
			return textField.getText();
		}
		return"";
	}
}.getText())