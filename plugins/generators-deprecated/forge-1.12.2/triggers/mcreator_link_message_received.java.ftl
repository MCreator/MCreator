@SubscribeEvent public void onMCreatorLinkMessageReceived(LinkCustomMessageReceivedEvent event){
		java.util.HashMap<String, Object> dependencies=new java.util.HashMap<>();
		dependencies.put("message",new String(event.getData()));
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
		}