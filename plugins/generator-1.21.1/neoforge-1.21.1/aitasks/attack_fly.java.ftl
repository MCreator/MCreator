<#-- @formatter:off -->
<#include "procedures.java.ftl">
<#if field$condition?has_content>
	<#assign conditions = generator.procedureNamesToObjects(field$condition)>
<#else>
	<#assign conditions = ["", ""]>
</#if>
this.goalSelector.addGoal(${cbi+1}, new Goal() {
	{
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	public boolean canUse() {
		if (${name}Entity.this.getTarget() != null && !${name}Entity.this.getMoveControl().hasWanted()) {
			<#if hasProcedure(conditions[0])>
			double x = ${name}Entity.this.getX();
			double y = ${name}Entity.this.getY();
			double z = ${name}Entity.this.getZ();
			Entity entity = ${name}Entity.this;
			Level world = ${name}Entity.this.level();
			</#if>
			return <#if hasProcedure(conditions[0])><@procedureOBJToConditionCode conditions[0]/><#else>true</#if>;
		} else {
			return false;
		}
	}

	@Override public boolean canContinueToUse() {
		<#if hasProcedure(conditions[1])>
		double x = ${name}Entity.this.getX();
		double y = ${name}Entity.this.getY();
		double z = ${name}Entity.this.getZ();
		Entity entity = ${name}Entity.this;
		Level world = ${name}Entity.this.level();
		</#if>
		return <#if hasProcedure(conditions[1])><@procedureOBJToConditionCode conditions[1]/> &&</#if>
			${name}Entity.this.getMoveControl().hasWanted() && ${name}Entity.this.getTarget() != null && ${name}Entity.this.getTarget().isAlive();
	}

	@Override public void start() {
		LivingEntity livingentity = ${name}Entity.this.getTarget();
		Vec3 vec3d = livingentity.getEyePosition(1);
		${name}Entity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, ${field$speed});
	}

	@Override public void tick() {
		LivingEntity livingentity = ${name}Entity.this.getTarget();
		if (${name}Entity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
			${name}Entity.this.doHurtTarget(livingentity);
		} else {
			double d0 = ${name}Entity.this.distanceToSqr(livingentity);
			if (d0 < ${field$radius}) {
				Vec3 vec3d = livingentity.getEyePosition(1);
				${name}Entity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, ${field$speed});
			}
		}
	}
});
<#-- @formatter:on -->