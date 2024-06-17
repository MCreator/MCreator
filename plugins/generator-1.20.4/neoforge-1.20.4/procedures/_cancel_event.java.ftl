if(event instanceof ICancellableEvent _cancellable) {
	_cancellable.setCanceled(true);
} else if(event != null && event.hasResult()) {
	event.setResult(Event.Result.DENY);
}