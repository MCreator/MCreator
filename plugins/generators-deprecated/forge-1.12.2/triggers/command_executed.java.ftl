@SubscribeEvent public void onCommand(CommandEvent event){
		ICommandSender sender=event.getSender();
		Entity entity=sender.getCommandSenderEntity();
		if(entity!=null){
		int i=(int)sender.getPosition().getX();
		int j=(int)sender.getPosition().getY();
		int k=(int)sender.getPosition().getZ();
		String command=event.getCommand().getName();
		java.util.HashMap<String, Object> dependencies=new java.util.HashMap<>();
		dependencies.put("x" ,i);
		dependencies.put("y" ,j);
		dependencies.put("z" ,k);
		dependencies.put("world" ,entity.world);
		dependencies.put("entity" ,entity);
		dependencies.put("command" ,command);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
		}
		}