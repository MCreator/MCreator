(new Object(){
	public String getText(){
		TextFieldWidget _tf=(TextFieldWidget)guistate.get("text:${field$textfield}");
		if(_tf!=null){
			return _tf.getText();
		}
		return"";
	}
}.getText())