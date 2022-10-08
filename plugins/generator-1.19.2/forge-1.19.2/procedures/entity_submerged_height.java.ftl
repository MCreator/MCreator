(new Object() {
    public double getSubmergedHeight(Entity _entity) {
        for (TagKey<Fluid> _fldtag : Registry.FLUID.getTagNames().toList()) {
            if (_entity.level.getFluidState(entity.blockPosition()).is(_fldtag))
                return _entity.getFluidHeight(_fldtag);
        }
        return 0;
    }
}.getSubmergedHeight(${input$entity}))