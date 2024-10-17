<#-- @formatter:off -->
<#include "procedures.java.ftl">
<#if field$condition?has_content>
	<#assign conditions = generator.procedureNamesToObjects(field$condition)>
<#else>
	<#assign conditions = ["", ""]>
</#if>
this.goalSelector.addGoal(${customBlockIndex+1}, new Goal() {
	{
		this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	public boolean shouldExecute() {
		if (CustomEntity.this.getAttackTarget() != null && !CustomEntity.this.getMoveHelper().isUpdating()) {
			<#if hasCondition(conditions[0])>
            double x = CustomEntity.this.posX;
			double y = CustomEntity.this.posY;
			double z = CustomEntity.this.posZ;
			Entity entity = CustomEntity.this;
			</#if>
			return <#if hasCondition(conditions[0])><@procedureOBJToConditionCode conditions[0]/><#else>true</#if>;
		} else {
			return false;
		}
	}

	@Override public boolean shouldContinueExecuting() {
		<#if hasCondition(conditions[1])>
		double x = CustomEntity.this.posX;
		double y = CustomEntity.this.posY;
		double z = CustomEntity.this.posZ;
		Entity entity = CustomEntity.this;
		</#if>
		return <#if hasCondition(conditions[1])><@procedureOBJToConditionCode conditions[1]/> &&</#if>
			CustomEntity.this.getMoveHelper().isUpdating() && CustomEntity.this.getAttackTarget() != null && CustomEntity.this.getAttackTarget().isAlive();
	}

	@Override public void startExecuting() {
		LivingEntity livingentity = CustomEntity.this.getAttackTarget();
		Vec3d vec3d = livingentity.getEyePosition(1);
		CustomEntity.this.moveController.setMoveTo(vec3d.x, vec3d.y, vec3d.z, ${field$speed});
	}

	@Override public void tick() {
		LivingEntity livingentity = CustomEntity.this.getAttackTarget();
		if (CustomEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
			CustomEntity.this.attackEntityAsMob(livingentity);
		} else {
			double d0 = CustomEntity.this.getDistanceSq(livingentity);
			if (d0 < ${field$radius}) {
				Vec3d vec3d = livingentity.getEyePosition(1);
				CustomEntity.this.moveController.setMoveTo(vec3d.x, vec3d.y, vec3d.z, ${field$speed});
			}
		}
	}
});
<#-- @formatter:on -->