<#include "procedures.java.ftl">

<#-- Item-related triggers -->
<#macro onEntitySwing procedure="">
<#if hasProcedure(procedure)>
@Override public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
	boolean retval = super.onEntitySwing(itemstack, entity);
	<@procedureCode procedure, {
		"x": "entity.getX()",
		"y": "entity.getY()",
		"z": "entity.getZ()",
		"world": "entity.level",
		"entity": "entity",
		"itemstack": "itemstack"
	}/>
	return retval;
}
</#if>
</#macro>

<#macro onCrafted procedure="">
<#if hasProcedure(procedure)>
@Override public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
	super.onCraftedBy(itemstack, world, entity);
	<@procedureCode data.onCrafted, {
		"x": "entity.getX()",
		"y": "entity.getY()",
		"z": "entity.getZ()",
		"world": "world",
		"entity": "entity",
		"itemstack": "itemstack"
	}/>
}
</#if>
</#macro>

<#macro onStoppedUsing procedure="">
<#if hasProcedure(procedure)>
@Override public void releaseUsing(ItemStack itemstack, Level world, LivingEntity entity, int time) {
	<@procedureCode data.onStoppedUsing, {
		"x": "entity.getX()",
		"y": "entity.getY()",
		"z": "entity.getZ()",
		"world": "world",
		"entity": "entity",
		"itemstack": "itemstack",
		"time": "time"
	}/>
}
</#if>
</#macro>

<#macro onEntityHitWith procedure="" hurtStack=false>
<#if hasProcedure(procedure) || hurtStack>
@Override public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
	<#if hurtStack>
		itemstack.hurtAndBreak(2, entity, i -> i.broadcastBreakEvent(EquipmentSlot.MAINHAND));
	<#else>
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
	</#if>
	<#if hasProcedure(procedure)>
		<@procedureCode procedure, {
			"x": "entity.getX()",
			"y": "entity.getY()",
			"z": "entity.getZ()",
			"world": "entity.level",
			"entity": "entity",
			"sourceentity": "sourceentity",
			"itemstack": "itemstack"
		}/>
	</#if>
	return <#if hurtStack>true<#else>retval</#if>;
}
</#if>
</#macro>

<#macro onRightClickedInAir procedure="">
<#if hasProcedure(procedure)>
@Override public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
	InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
	<@procedureCode procedure, {
		"x": "entity.getX()",
		"y": "entity.getY()",
		"z": "entity.getZ()",
		"world": "world",
		"entity": "entity",
		"itemstack": "ar.getObject()"
	}/>
	return ar;
}
</#if>
</#macro>

<#macro onItemTick inUseProcedure="" inInvProcedure="">
<#if hasProcedure(inUseProcedure) || hasProcedure(inInvProcedure)>
@Override public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
	super.inventoryTick(itemstack, world, entity, slot, selected);
	<#if hasProcedure(inUseProcedure)>
	if (selected)
		<@procedureCode inUseProcedure, {
			"x": "entity.getX()",
			"y": "entity.getY()",
			"z": "entity.getZ()",
			"world": "world",
			"entity": "entity",
			"itemstack": "itemstack",
			"slot": "slot"
		}/>
	</#if>
	<#if hasProcedure(inInvProcedure)>
		<@procedureCode inInvProcedure, {
			"x": "entity.getX()",
			"y": "entity.getY()",
			"z": "entity.getZ()",
			"world": "world",
			"entity": "entity",
			"itemstack": "itemstack",
			"slot": "slot"
		}/>
	</#if>
}
</#if>
</#macro>

<#macro onItemUsedOnBlock procedure="">
<#if hasProcedure(procedure)>
@Override public InteractionResult useOn(UseOnContext context) {
	super.useOn(context);
	<@procedureCodeWithOptResult procedure, "actionresulttype", "InteractionResult.SUCCESS", {
		"world": "context.getLevel()",
		"x": "context.getClickedPos().getX()",
		"y": "context.getClickedPos().getY()",
		"z": "context.getClickedPos().getZ()",
		"blockstate": "context.getLevel().getBlockState(context.getClickedPos())",
		"entity": "context.getPlayer()",
		"direction": "context.getClickedFace()",
		"itemstack": "context.getItemInHand()"
	}/>
}
</#if>
</#macro>

<#-- Armor triggers -->
<#macro onArmorTick procedure="">
<#if hasProcedure(procedure)>
@Override public void onArmorTick(ItemStack itemstack, Level world, Player entity) {
	<@procedureCode procedure, {
		"x": "entity.getX()",
		"y": "entity.getY()",
		"z": "entity.getZ()",
		"world": "world",
		"entity": "entity",
		"itemstack": "itemstack"
	}/>
}
</#if>
</#macro>