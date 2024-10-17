(new Object() {
    public double getSubmergedHeight(Entity _entity) {
        for (ITag.INamedTag<Fluid> _fldtag : FluidTags.getAllTags()) {
            if (_fldtag.getName().equals(_entity.world.getFluidState(entity.getPosition()).getFluid().getRegistryName())) {
                return _entity.func_233571_b_(_fldtag);
            }
        }
        return 0;
    }
}.getSubmergedHeight(${input$entity}.getEntity()))