@SubscribeEvent public void onMCreatorLinkPinChanged(LinkDigitalPinChangedEvent event){
		java.util.HashMap<String, Object> dependencies=new java.util.HashMap<>();
		dependencies.put("pin",event.getPin());
		dependencies.put("value",(int)event.getValue());
		dependencies.put("risingEdge",event.isRisingEdge());
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
		}