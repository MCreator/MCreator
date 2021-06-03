new Object() {
    public double getSubmergedHeight(Entity entity) {
        for (ITag.INamedTag<Fluid> tag : FluidTags.getAllTags()) {
            if (tag.getName().equals(entity.world.getFluidState(entity.getPosition()).getFluid().getRegistryName())) {
                return entity.func_233571_b_(tag);
            }
        }
        return 0;
    }
}.getSubmergedHeight(${input$entity}.getEntity())
