<#macro onBlockDestroyedWith procedure="" hurtStack=false>
<#if hasProcedure(procedure) || hurtStack>
@Override public boolean mineBlock(ItemStack itemstack, Level world, BlockState blockstate, BlockPos pos, LivingEntity entity) {
	<#if hurtStack>
		itemstack.hurtAndBreak(1, entity, i -> i.broadcastBreakEvent(EquipmentSlot.MAINHAND));
	<#else>
		boolean retval = super.mineBlock(itemstack,world,blockstate,pos,entity);
	</#if>
	<#if hasProcedure(procedure)>
		<@procedureCode procedure, {
			"x": "pos.getX()",
			"y": "pos.getY()",
			"z": "pos.getZ()",
			"world": "world",
			"entity": "entity",
			"itemstack": "itemstack",
			"blockstate": "blockstate"
		}/>
	</#if>
	return <#if hurtStack>true<#else>retval</#if>;
}
</#if>
</#macro>

<#macro onDroppedByPlayer procedure="">
<#if hasProcedure(procedure)>
@Override public boolean onDroppedByPlayer(ItemStack itemstack, Player entity) {
	<@procedureCode procedure, {
		"x": "entity.getX()",
		"y": "entity.getY()",
		"z": "entity.getZ()",
		"world": "entity.level()",
		"entity": "entity",
		"itemstack": "itemstack"
	}/>
	return true;
}
</#if>
</#macro>