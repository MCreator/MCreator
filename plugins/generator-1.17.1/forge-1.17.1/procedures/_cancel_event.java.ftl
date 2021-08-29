if(dependencies.get("event")!=null){
	Object _obj = dependencies.get("event");
	if(_obj instanceof Event) {
		Event _evt = (Event) _obj;
		if(_evt.isCancelable())
			_evt.setCanceled(true);
	}
}