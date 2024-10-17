(new Object() {
	public Direction getDirection(BlockPos pos){
		try {
			BlockState _bs = world.getBlockState(pos);
			DirectionProperty property = (DirectionProperty) _bs.getBlock().getStateContainer().getProperty("facing");
			if (property != null)
				return _bs.get(property);
			return Direction.getFacingFromAxisDirection(_bs.get((EnumProperty<Direction.Axis>) _bs.getBlock()
				.getStateContainer().getProperty("axis")), Direction.AxisDirection.POSITIVE);
		} catch (Exception e) {
			return Direction.NORTH;
		}
}}.getDirection(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})))