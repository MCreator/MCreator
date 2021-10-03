/*@ItemStack*/(new Object(){
	public ItemStack getItemStack(int sltid) {
		if(${input$entity} instanceof ServerPlayer _player) {
			AbstractContainerMenu _current = _player.containerMenu;
			if(_current instanceof Supplier) {
				Object invobj = ((Supplier) _current).get();
				if(invobj instanceof Map) {
					return ((Slot) ((Map) invobj).get(sltid)).getItem();
				}
			}
		}
		return ItemStack.EMPTY;
	}
}.getItemStack((int)(${input$slotid})))