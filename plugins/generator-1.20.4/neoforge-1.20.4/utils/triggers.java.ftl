<#include "procedures.java.ftl">

<#-- Item-related triggers -->
<#macro addSpecialInformation procedure="" isBlock=false>
	<#if procedure?has_content || hasProcedure(procedure)>
		@Override public void appendHoverText(ItemStack itemstack, <#if isBlock>BlockGetter<#else>Level</#if> level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		<#if hasProcedure(procedure)>
			Entity entity = itemstack.getEntityRepresentation();
			list.add(Component.literal(<@procedureCode procedure, {
				"x": "entity != null ? entity.getX() : 0.0",
				"y": "entity != null ? entity.getY() : 0.0",
				"z": "entity != null ? entity.getZ() : 0.0",
				"entity": "entity",
				"world": "level instanceof Level ? (LevelAccessor) level : null"
			}, false/>));
		<#else>
			<#list procedure.getFixedValue() as entry>
				list.add(Component.literal("${JavaConventions.escapeStringForJava(entry)}"));
			</#list>
		</#if>
		}
	</#if>
</#macro>

<#macro onEntitySwing procedure="">
<#if hasProcedure(procedure)>
@Override public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
	boolean retval = super.onEntitySwing(itemstack, entity);
	<@procedureCode procedure, {
		"x": "entity.getX()",
		"y": "entity.getY()",
		"z": "entity.getZ()",
		"world": "entity.level()",
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
			"world": "entity.level()",
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

<#macro hasGlow procedure="">
<#if procedure?has_content && (hasProcedure(procedure) || procedure.getFixedValue())>
@Override @OnlyIn(Dist.CLIENT) public boolean isFoil(ItemStack itemstack) {
	<#if hasProcedure(procedure)>
		<#assign dependencies = procedure.getDependencies(generator.getWorkspace())>
		<#if !(dependencies.isEmpty() || (dependencies.size() == 1 && dependencies.get(0).getName() == "itemstack"))>
		Entity entity = Minecraft.getInstance().player;
		</#if>
		return <@procedureCode procedure, {
			"x": "entity.getX()",
			"y": "entity.getY()",
			"z": "entity.getZ()",
			"entity": "entity",
			"world": "entity.level()",
			"itemstack": "itemstack"
		}/>
	<#else>
		return true;
	</#if>
}
</#if>
</#macro>

<#-- Armor triggers -->
<#macro onArmorTick procedure="">
<#if hasProcedure(procedure)>
<#-- ideally we would use inventoryTick for slot [36, 39], however slot number does not seem to work in NF 1.20.4 -->
@Override public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
	super.inventoryTick(itemstack, world, entity, slot, selected);
	if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
		<@procedureCode procedure, {
		"x": "entity.getX()",
		"y": "entity.getY()",
		"z": "entity.getZ()",
		"world": "world",
		"entity": "entity",
		"itemstack": "itemstack"
		}/>
	}
}
</#if>
</#macro>

<#macro piglinNeutral procedure="">
<#if procedure?has_content || hasProcedure(procedure)>
@Override public boolean makesPiglinsNeutral(ItemStack itemstack, LivingEntity entity) {
	<#if hasProcedure(procedure)>
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		Level world = entity.level();
	</#if>
	return <@procedureOBJToConditionCode procedure procedure.getFixedValue() false/>;
}
</#if>
</#macro>
