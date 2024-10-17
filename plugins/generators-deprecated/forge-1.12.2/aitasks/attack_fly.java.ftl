<#-- @formatter:off -->
this.tasks.addTask(${customBlockIndex+1}, new EntityAIBase() {
	{
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if (EntityCustom.this.getAttackTarget() != null && !EntityCustom.this.getMoveHelper().isUpdating()) {
			return true;
		} else {
			return false;
		}
	}

	@Override public boolean shouldContinueExecuting() {
		return EntityCustom.this.getMoveHelper().isUpdating() && EntityCustom.this.getAttackTarget() != null && EntityCustom.this.getAttackTarget().isEntityAlive();
	}

	@Override public void startExecuting() {
		EntityLivingBase livingentity = EntityCustom.this.getAttackTarget();
		Vec3d vec3d = livingentity.getPositionEyes(1);
		EntityCustom.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, ${field$speed});
	}

	@Override public void updateTask() {
		EntityLivingBase livingentity = EntityCustom.this.getAttackTarget();
		double d0 = EntityCustom.this.getDistanceSq(livingentity);
		if (d0 <= getAttackReachSq(livingentity)) {
			EntityCustom.this.attackEntityAsMob(livingentity);
		} else if (d0 < ${field$radius}) {
			Vec3d vec3d = livingentity.getPositionEyes(1);
			EntityCustom.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, ${field$speed});
		}
	}

	protected double getAttackReachSq(EntityLivingBase attackTarget)	{
		return EntityCustom.this.width * 1.5 * EntityCustom.this.height * 1.5 + attackTarget.height;
	}

});
<#-- @formatter:on -->