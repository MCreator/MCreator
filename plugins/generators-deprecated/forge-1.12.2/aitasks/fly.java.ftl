<#-- @formatter:off -->
this.tasks.addTask(${customBlockIndex+1}, new EntityAIWander(this, ${field$speed}, 20) {

    @Override protected Vec3d getPosition() {
		Random random = EntityCustom.this.getRNG();
		double dir_x = EntityCustom.this.posX + ((random.nextFloat() * 2 - 1) * 16);
		double dir_y = EntityCustom.this.posY + ((random.nextFloat() * 2 - 1) * 16);
		double dir_z = EntityCustom.this.posZ + ((random.nextFloat() * 2 - 1) * 16);
		return new Vec3d(dir_x, dir_y, dir_z);
    }

});
<#-- @formatter:on -->