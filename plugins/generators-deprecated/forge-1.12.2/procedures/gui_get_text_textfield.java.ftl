(new Object(){
	public String getText(){
		GuiTextField textField=(GuiTextField)guistate.get("text:${field$textfield}");
		if(textField!=null){
		return textField.getText();
		}
		return"";
		}
		}.getText())