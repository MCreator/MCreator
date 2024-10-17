if(event != null && event.isCancelable()) {
	event.setCanceled(true);
} else if(event != null && event.hasResult()) {
	event.setResult(Event.Result.DENY);
}