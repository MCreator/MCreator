/*@ItemStack*/(new Object(){
	public ItemStack entityToItem(Entity _ent) {
		if (_ent instanceof ItemEntity) {
            return ((ItemEntity) _ent).getItem();
        }
		return ItemStack.EMPTY;
	}
}.entityToItem(${input$source}))