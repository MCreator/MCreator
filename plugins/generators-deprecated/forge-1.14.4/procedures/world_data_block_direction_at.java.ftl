(new Object() {
	public Direction getDirection(BlockPos pos){
		try {
			BlockState _bs = world.getBlockState(pos);
		    DirectionProperty property = (DirectionProperty) _bs.getBlock().getStateContainer().getProperty("facing");
		    return _bs.get(property);
        } catch (Exception e) {
		    return Direction.NORTH;
        }
	}}.getDirection(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})))