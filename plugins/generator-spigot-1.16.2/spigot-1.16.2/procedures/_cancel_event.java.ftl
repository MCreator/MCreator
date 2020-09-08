if(dependencies.get("event")!=null){
	Object _obj = dependencies.get("event");
	if(_obj instanceof Cancellable) {
		((Cancellable) _obj).setCancelled(true);
	}
}