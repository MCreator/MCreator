(TierSortingRegistry.getSortedTiers().stream().filter(t -> t.getTag()
        .contains(world.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})).getBlock()))
        .map(Tier::getLevel).findFirst().orElse(0))