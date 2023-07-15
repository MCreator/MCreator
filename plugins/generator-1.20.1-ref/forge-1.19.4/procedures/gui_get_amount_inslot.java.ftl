/*@int*/(new Object(){
	public int getAmount(int sltid) {
		if(${input$entity} instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
			ItemStack stack = ((Slot) _slots.get(sltid)).getItem();
			if(stack != null)
				return stack.getCount();
		}
		return 0;
	}
}.getAmount(${opt.toInt(input$slotid)}))