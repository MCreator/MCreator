(new Object() {
    public double getSubmergedHeight(Entity _entity) {
		for (net.minecraft.tags.Tag<Fluid> _fldtag : FluidTags.getStaticTags()) {
		    if (_entity.level.getFluidState(entity.blockPosition()).is(_fldtag))
		        return _entity.getFluidHeight(_fldtag);
		    }
		return 0;
    }
}.getSubmergedHeight(${input$entity}))