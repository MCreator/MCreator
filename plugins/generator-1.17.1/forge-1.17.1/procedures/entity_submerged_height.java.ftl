new Object() {
    public double getSubmergedHeight(Entity entity) {
        for (net.minecraft.tags.Tag<Fluid> tag : FluidTags.getStaticTags()) {
            if (entity.level.getFluidState(entity.blockPosition()).is(tag)) {
                return entity.getFluidHeight(tag);
            }
        }
        return 0;
    }
}.getSubmergedHeight(${input$entity})