if(dependencies.get("event")!=null){
	Object _obj = dependencies.get("event");
	if(_obj instanceof Event) {
		Event _evt = (Event) _obj;
		if(_evt.hasResult())
			_evt.setResult(Event.Result.${result});
	}
}