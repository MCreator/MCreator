(new Object() {
    public boolean lookingAtBlock(Entity _ent, double maxDist) {
        return _ent.level.clip(new ClipContext(_ent.getEyePosition(1f), _ent.getEyePosition(1f).add(_ent.getViewVector(1f).scale(maxDist)),
        ClipContext.Block.${field$block_mode}, ClipContext.Fluid.${field$fluid_mode}, _ent)).getType() == HitResult.Type.BLOCK;
    }
}.lookingAtBlock(${input$entity}, ${input$maxdistance}))