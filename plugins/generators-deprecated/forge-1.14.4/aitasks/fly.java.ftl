<#-- @formatter:off -->
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new RandomWalkingGoal(this, ${field$speed}, 20) {

    @Override protected Vec3d getPosition() {
		Random random = CustomEntity.this.getRNG();
		double dir_x = CustomEntity.this.posX + ((random.nextFloat() * 2 - 1) * 16);
		double dir_y = CustomEntity.this.posY + ((random.nextFloat() * 2 - 1) * 16);
		double dir_z = CustomEntity.this.posZ + ((random.nextFloat() * 2 - 1) * 16);
		return new Vec3d(dir_x, dir_y, dir_z);
	}

	<@conditionCode field$condition false/>

});
<#-- @formatter:on -->