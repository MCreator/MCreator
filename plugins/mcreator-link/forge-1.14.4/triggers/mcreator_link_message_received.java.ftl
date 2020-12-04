@SubscribeEvent public void onMCreatorLinkMessageReceived(LinkCustomMessageReceivedEvent event){
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("message",new String(event.getData()));
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
		}