(new Object() {
    public double getSubmergedHeight(Entity _entity) {
        for (FluidType fluidType : NeoForgeRegistries.FLUID_TYPES) {
            if (_entity.level().getFluidState(_entity.blockPosition()).getFluidType() == fluidType)
                return _entity.getFluidTypeHeight(fluidType);
        }
        return 0;
    }
}.getSubmergedHeight(${input$entity}))