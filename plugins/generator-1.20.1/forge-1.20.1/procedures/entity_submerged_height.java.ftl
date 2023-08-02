(new Object() {
    public double getSubmergedHeight(Entity _entity) {
        for (FluidType fluidType : ForgeRegistries.FLUID_TYPES.get().getValues()) {
            if (_entity.level().getFluidState(_entity.blockPosition()).getFluidType() == fluidType)
                return _entity.getFluidTypeHeight(fluidType);
        }
        return 0;
    }
}.getSubmergedHeight(${input$entity}))