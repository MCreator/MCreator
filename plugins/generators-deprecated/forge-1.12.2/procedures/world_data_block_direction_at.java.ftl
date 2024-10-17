(new Object() {
	public EnumFacing getEnumFacing(BlockPos pos){
		try {
			IBlockState _bs = world.getBlockState(pos);
			for (IProperty<?> prop : _bs.getProperties().keySet()) {
				if (prop.getName().equals("facing"))
					return _bs.getValue((PropertyDirection) prop);
			}
			return EnumFacing.NORTH;
        } catch (Exception e) {
		    return EnumFacing.NORTH;
        }
	}}.getEnumFacing(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})))