(new Object(){
	public int getAmount(int sltid) {
		if(${input$entity} instanceof ServerPlayer _player) {
			AbstractContainerMenu _current = _player.containerMenu;
			if(_current instanceof Supplier) {
				Object invobj = ((Supplier) _current).get();
				if(invobj instanceof Map) {
					ItemStack stack = ((Slot) ((Map) invobj).get(sltid)).getItem();
					if(stack != null)
						return stack.getCount();
				}
			}
		}
		return 0;
	}
}.getAmount((int)(${input$slotid})))