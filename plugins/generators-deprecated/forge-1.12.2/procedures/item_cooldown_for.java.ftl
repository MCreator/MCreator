if(entity instanceof EntityPlayer)
	((EntityPlayer)entity).getCooldownTracker().setCooldown(itemstack.getItem(), (int) ${input$ticks});