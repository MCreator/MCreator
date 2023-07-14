<#-- Block-related triggers -->
<#macro onDestroyedByPlayer procedure="">
<#if hasProcedure(procedure)>
@Override public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
			boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
	<@procedureCode procedure, {
	"x": "pos.getX()",
	"y": "pos.getY()",
	"z": "pos.getZ()",
	"world": "world",
	"entity": "entity",
	"blockstate": "blockstate"
	}/>
	return retval;
}
</#if>
</#macro>

<#macro onEntityWalksOn procedure="">
<#if hasProcedure(procedure)>
@Override public void stepOn(Level world, BlockPos pos, BlockState blockstate, Entity entity) {
			super.stepOn(world, pos, blockstate, entity);
	<@procedureCode procedure, {
	"x": "pos.getX()",
	"y": "pos.getY()",
	"z": "pos.getZ()",
	"world": "world",
	"entity": "entity",
	"blockstate": "blockstate"
	}/>
}
</#if>
</#macro>

<#macro onBlockPlacedBy procedure="">
<#if hasProcedure(procedure)>
@Override public void setPlacedBy(Level world, BlockPos pos, BlockState blockstate, LivingEntity entity, ItemStack itemstack) {
			super.setPlacedBy(world, pos, blockstate, entity, itemstack);
	<@procedureCode procedure, {
	"x": "pos.getX()",
	"y": "pos.getY()",
	"z": "pos.getZ()",
	"world": "world",
	"entity": "entity",
	"blockstate": "blockstate",
	"itemstack": "itemstack"
	}/>
}
</#if>
</#macro>

<#macro onStartToDestroy procedure="">
<#if hasProcedure(procedure)>
@Override public void attack(BlockState blockstate, Level world, BlockPos pos, Player entity) {
			super.attack(blockstate, world, pos, entity);
	<@procedureCode procedure, {
	"x": "pos.getX()",
	"y": "pos.getY()",
	"z": "pos.getZ()",
	"world": "world",
	"entity": "entity",
	"blockstate": "blockstate"
	}/>
}
</#if>
</#macro>

<#macro onBlockRightClicked procedure="">
<#if hasProcedure(procedure)>
@Override public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
			super.use(blockstate, world, pos, entity, hand, hit);
	<@procedureCodeWithOptResult procedure, "actionresulttype", "InteractionResult.SUCCESS", {
	"x": "pos.getX()",
	"y": "pos.getY()",
	"z": "pos.getZ()",
	"world": "world",
	"blockstate": "blockstate",
	"entity": "entity",
	"direction": "hit.getDirection()",
	"hitX": "hit.getLocation().x()",
	"hitY": "hit.getLocation().y()",
	"hitZ": "hit.getLocation().z()"
	}/>
}
</#if>
</#macro>

<#macro onHitByProjectile procedure="">
<#if hasProcedure(procedure)>
@Override public void onProjectileHit(Level world, BlockState blockstate, BlockHitResult hit, Projectile entity) {
	<@procedureCode procedure, {
	"x": "hit.getBlockPos().getX()",
	"y": "hit.getBlockPos().getY()",
	"z": "hit.getBlockPos().getZ()",
	"world": "world",
	"blockstate": "blockstate",
	"entity": "entity",
	"direction": "hit.getDirection()",
	"hitX": "hit.getLocation().x()",
	"hitY": "hit.getLocation().y()",
	"hitZ": "hit.getLocation().z()"
	}/>
}
</#if>
</#macro>

<#macro bonemealEvents isBonemealTargetCondition="" bonemealSuccessCondition="" onBonemealSuccess="">
@Override public boolean isValidBonemealTarget(LevelReader worldIn, BlockPos pos, BlockState blockstate, boolean clientSide) {
	<#if hasProcedure(isBonemealTargetCondition)>
	if (worldIn instanceof LevelAccessor world) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return <@procedureOBJToConditionCode isBonemealTargetCondition/>;
	}
	return false;
	<#else>
	return true;
	</#if>
}

@Override public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState blockstate) {
	<#if hasProcedure(bonemealSuccessCondition)>
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return <@procedureOBJToConditionCode bonemealSuccessCondition/>;
	<#else>
	return true;
	</#if>
}

@Override public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState blockstate) {
	<#if hasProcedure(onBonemealSuccess)>
	<@procedureCode onBonemealSuccess, {
	"x": "pos.getX()",
	"y": "pos.getY()",
	"z": "pos.getZ()",
	"world": "world",
	"blockstate": "blockstate"
	}/>
	</#if>
}
</#macro>