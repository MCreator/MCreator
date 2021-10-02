if(${input$entity} instanceof ServerPlayer _player) {
	AbstractContainerMenu _current = _player.containerMenu;
	if(_current instanceof Supplier) {
		Object invobj = ((Supplier) _current).get();
		if(invobj instanceof Map) {
			ItemStack stack=((Slot) ((Map) invobj).get((int)(${input$slotid}))).getItem();
    		if(stack != null) {
    			if(stack.hurt((int) ${input$amount},new Random(),null)){
    				stack.shrink(1);
    				stack.setDamageValue(0);
				}
    			_current.broadcastChanges();
    		}
		}
	}
}