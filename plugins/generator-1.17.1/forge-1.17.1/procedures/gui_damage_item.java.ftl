if(${input$entity} instanceof ServerPlayer _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
	ItemStack stack=((Slot) _slots.get(${opt.toInt(input$slotid)})).getItem();
    if(stack != null) {
    	if(stack.hurt(${opt.toInt(input$amount)},new Random(),null)){
    		stack.shrink(1);
    		stack.setDamageValue(0);
		}
    	_player.containerMenu.broadcastChanges();
    }
}