/*@int*/(new Object(){
	public int getAmount(int _slotid, Entity _ent) {
		if(_ent instanceof ServerPlayer _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
			ItemStack _stack = ((Slot) _slots.get(_slotid)).getItem();
			if(_stack != null)
				return _stack.getCount();
		}
		return 0;
	}
}.getAmount(${opt.toInt(input$slotid)}, ${input$entity}))