{
	TextFieldWidget textField = (TextFieldWidget) guistate.get("text:${field$textfield}");
	if (textField != null) {
		textField.setTextColor(${input$dyeColor}.getTextColor());
	}
}