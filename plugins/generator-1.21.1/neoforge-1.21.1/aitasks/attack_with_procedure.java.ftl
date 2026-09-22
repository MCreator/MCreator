<#-- @formatter:off -->
<#include "procedures.java.ftl">
<#if field$condition?has_content>
	<#assign conditions = generator.procedureNamesToObjects(field$condition)>
<#else>
	<#assign conditions = ["", ""]>
</#if>
<#assign attackProcedure = generator.procedureNamesToObjects(field$procedure)[0]>
this.goalSelector.addGoal(${cbi+1}, new Goal() {
	{
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	private int cooldown = 0;

	@Override public boolean canUse() {
		LivingEntity livingentity = ${name}Entity.this.getTarget();
		if (livingentity != null && livingentity.isAlive()) {
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
			${name}Entity.this.getTarget() != null && ${name}Entity.this.getTarget().isAlive();
	}

	@Override public void start() {
		this.cooldown = 0;
	}

	@Override public void stop() {
		${name}Entity.this.getNavigation().stop();
	}

	@Override public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override public void tick() {
		LivingEntity livingentity = ${name}Entity.this.getTarget();
		boolean inRange = ${name}Entity.this.distanceToSqr(livingentity) <= ${field$range?number * field$range?number}
			&& ${name}Entity.this.getSensing().hasLineOfSight(livingentity);
		${name}Entity.this.getLookControl().setLookAt(livingentity, 30, 30);
		if (inRange) {
			${name}Entity.this.getNavigation().stop();
		} else {
			${name}Entity.this.getNavigation().moveTo(livingentity, ${field$speed});
		}
		if (--this.cooldown <= 0 && inRange) {
			this.cooldown = ${field$interval};
			<#if hasProcedure(attackProcedure)>
			double x = ${name}Entity.this.getX();
			double y = ${name}Entity.this.getY();
			double z = ${name}Entity.this.getZ();
			Entity entity = ${name}Entity.this;
			Entity sourceentity = livingentity;
			Level world = ${name}Entity.this.level();
			<@procedureOBJToCode attackProcedure/>
			</#if>
		}
	}
});
<#-- @formatter:on -->
