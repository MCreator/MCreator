<#-- @formatter:off -->
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new RandomStrollGoal(this, ${field$speed}, 20) {

    @Override protected Vec3 getPosition() {
		RandomSource random = ${name}Entity.this.getRandom();
		double dir_x = ${name}Entity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
		double dir_y = ${name}Entity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
		double dir_z = ${name}Entity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
		return new Vec3(dir_x, dir_y, dir_z);
	}

	<@conditionCode field$condition false/>

});
<#-- @formatter:on -->