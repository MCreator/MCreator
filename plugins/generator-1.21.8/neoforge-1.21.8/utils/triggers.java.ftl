<#include "procedures.java.ftl">

<#-- Armor triggers -->
<#macro onArmorTick procedure="">
<#if hasProcedure(procedure)>
<#-- ideally we would use inventoryTick for slot [36, 39], however slot number does not seem to work in NF 1.20.4 -->
@Override public void inventoryTick(ItemStack itemstack, ServerLevel world, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
	super.inventoryTick(itemstack, world, entity, equipmentSlot);
	if (entity instanceof Player player && (equipmentSlot != null && equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR)) {
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
<#if procedure?has_content && (hasProcedure(procedure) || procedure.getFixedValue())>
@Override public boolean makesPiglinsNeutral(ItemStack itemstack, LivingEntity entity) {
	<#if hasProcedure(procedure)>
		return <@procedureCode procedure, {
			"x": "entity.getX()",
			"y": "entity.getY()",
			"z": "entity.getZ()",
			"world": "entity.level()",
			"entity": "entity",
			"itemstack": "itemstack"
		}/>
	<#else>
		return true;
	</#if>
}
</#if>
</#macro>

<#-- Item-related triggers -->
<#macro addSpecialInformation procedure="" translationKeyHeader="" isBlock=false>
	<#if procedure?has_content && (hasProcedure(procedure) || !procedure.getFixedValue().isEmpty())>
		@Override public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> componentConsumer, TooltipFlag flag) {
			super.appendHoverText(itemstack, context, tooltipDisplay, componentConsumer, flag);
		<#if hasProcedure(procedure)>
			Entity entity = itemstack.getEntityRepresentation() != null ? itemstack.getEntityRepresentation() : ${JavaModName}.clientPlayer();
			String hoverText = <@procedureCode procedure, {
				"x": "entity.getX()",
				"y": "entity.getY()",
				"z": "entity.getZ()",
				"entity": "entity",
				"world": "entity.level()",
				"itemstack": "itemstack"
			}, false/>;
			if (hoverText != null) {
				for (String line : hoverText.split("\n")) {
					componentConsumer.accept(Component.literal(line));
				}
			}
		<#elseif translationKeyHeader?has_content>
			<#list procedure.getFixedValue() as entry>
			componentConsumer.accept(Component.translatable("${translationKeyHeader}.description_${entry?index}"));
			</#list>
		<#else>
			<#list procedure.getFixedValue() as entry>
			componentConsumer.accept(Component.literal("${JavaConventions.escapeStringForJava(entry)}"));
			</#list>
		</#if>
		}
	</#if>
</#macro>

<#macro onEntitySwing procedure="">
<#if hasProcedure(procedure)>
@Override public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity, InteractionHand hand) {
	boolean retval = super.onEntitySwing(itemstack, entity, hand);
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
@Override public void onCraftedBy(ItemStack itemstack, Player entity) {
	super.onCraftedBy(itemstack, entity);
	<@procedureCode data.onCrafted, {
		"x": "entity.getX()",
		"y": "entity.getY()",
		"z": "entity.getZ()",
		"world": "entity.level()",
		"entity": "entity",
		"itemstack": "itemstack"
	}/>
}
</#if>
</#macro>

<#macro onEntityHitWith procedure="" hurtStack=false hurtStackAmount=2>
<#if hasProcedure(procedure) || hurtStack>
@Override public void hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
	<#if hurtStack>
		itemstack.hurtAndBreak(${hurtStackAmount}, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
	<#else>
		super.hurtEnemy(itemstack, entity, sourceentity);
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
}
</#if>
</#macro>

<#macro onRightClickedInAir procedure="">
<#if hasProcedure(procedure)>
@Override public InteractionResult use(Level world, Player entity, InteractionHand hand) {
	InteractionResult ar = super.use(world, entity, hand);
	<@procedureCode procedure, {
		"x": "entity.getX()",
		"y": "entity.getY()",
		"z": "entity.getZ()",
		"world": "world",
		"entity": "entity",
		"itemstack": "entity.getItemInHand(hand)"
	}/>
	return ar;
}
</#if>
</#macro>

<#macro onItemTick inUseProcedure="" inInvProcedure="">
<#if hasProcedure(inUseProcedure) || hasProcedure(inInvProcedure)>
@Override public void inventoryTick(ItemStack itemstack, ServerLevel world, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
	super.inventoryTick(itemstack, world, entity, equipmentSlot);
	<#if hasProcedure(inUseProcedure)>
	if (equipmentSlot == EquipmentSlot.MAINHAND)
		<@procedureCode inUseProcedure, {
			"x": "entity.getX()",
			"y": "entity.getY()",
			"z": "entity.getZ()",
			"world": "world",
			"entity": "entity",
			"itemstack": "itemstack",
			"slot": "equipmentSlot != null ? equipmentSlot.getId() : -1"
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
			"slot": "equipmentSlot != null ? equipmentSlot.getId() : -1"
		}/>
	</#if>
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
@Override public boolean isFoil(ItemStack itemstack) {
	<#if hasProcedure(procedure)>
		<#assign dependencies = procedure.getDependencies(generator.getWorkspace())>
		<#if !(dependencies.isEmpty() || (dependencies.size() == 1 && dependencies.get(0).getName() == "itemstack"))>
		Entity entity = ${JavaModName}.clientPlayer();
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

<#macro onBlockDestroyedWith procedure="" hurtStack=false>
<#if hasProcedure(procedure) || hurtStack>
@Override public boolean mineBlock(ItemStack itemstack, Level world, BlockState blockstate, BlockPos pos, LivingEntity entity) {
	<#if hurtStack>
		itemstack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
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

<#macro onItemEntityDestroyed procedure="">
<#if hasProcedure(procedure)>
@Override public void onDestroyed(ItemEntity entity, DamageSource damagesource) {
	super.onDestroyed(entity, damagesource);
	<@procedureCode procedure, {
		"x": "entity.getX()",
		"y": "entity.getY()",
		"z": "entity.getZ()",
		"world": "entity.level()",
		"entity": "entity",
		"itemstack": "entity.getItem()",
		"damagesource": "damagesource"
	}/>
}
</#if>
</#macro>

<#-- Block-related triggers -->
<#macro onDestroyedByExplosion procedure="">
<#if hasProcedure(procedure)>
@Override public void wasExploded(ServerLevel world, BlockPos pos, Explosion e) {
	super.wasExploded(world, pos, e);
	<@procedureCode procedure, {
		"x": "pos.getX()",
		"y": "pos.getY()",
		"z": "pos.getZ()",
		"world": "world"
	}/>
}
</#if>
</#macro>

<#macro onEntityCollides procedure="">
<#if hasProcedure(procedure)>
@Override public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier) {
	super.entityInside(blockstate, world, pos, entity, insideBlockEffectApplier);
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

<#macro onBlockAdded procedure="" scheduleTick=false tickRate=0>
<#if scheduleTick || hasProcedure(procedure)>
@Override public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
	super.onPlace(blockstate, world, pos, oldState, moving);
	<#if scheduleTick>
		world.scheduleTick(pos, this, ${tickRate});
	</#if>
	<#if hasProcedure(procedure)>
		<@procedureCode procedure, {
			"x": "pos.getX()",
			"y": "pos.getY()",
			"z": "pos.getZ()",
			"world": "world",
			"blockstate": "blockstate",
			"oldState": "oldState",
			"moving": "moving"
			}/>
	</#if>
}
</#if>
</#macro>

<#macro onRedstoneOrNeighborChanged onRedstoneOn="" onRedstoneOff="" onNeighborChanged="">
<#if hasProcedure(onRedstoneOn) || hasProcedure(onRedstoneOff) || hasProcedure(onNeighborChanged)>
@Override public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean moving) {
	super.neighborChanged(blockstate, world, pos, neighborBlock, orientation, moving);
	<#if hasProcedure(onRedstoneOn) || hasProcedure(onRedstoneOff)>
		if (world.getBestNeighborSignal(pos) > 0) {
		<#if hasProcedure(onRedstoneOn)>
			<@procedureCode onRedstoneOn, {
				"x": "pos.getX()",
				"y": "pos.getY()",
				"z": "pos.getZ()",
				"world": "world",
				"blockstate": "blockstate"
			}/>
		</#if>
	}
		<#if hasProcedure(onRedstoneOff)> else {
			<@procedureCode onRedstoneOff, {
				"x": "pos.getX()",
				"y": "pos.getY()",
				"z": "pos.getZ()",
				"world": "world",
				"blockstate": "blockstate"
			}/>
		}
		</#if>
	</#if>
	<#if hasProcedure(onNeighborChanged)>
		<@procedureCode onNeighborChanged, {
			"x": "pos.getX()",
			"y": "pos.getY()",
			"z": "pos.getZ()",
			"world": "world",
			"blockstate": "blockstate"
		}/>
	</#if>
}
</#if>
</#macro>

<#macro onAnimateTick procedure="">
<#if hasProcedure(procedure)>
@Override public void animateTick(BlockState blockstate, Level world, BlockPos pos, RandomSource random) {
	super.animateTick(blockstate, world, pos, random);
	<@procedureCode procedure, {
		"x": "pos.getX()",
		"y": "pos.getY()",
		"z": "pos.getZ()",
		"world": "world",
		"entity": (JavaModName + ".clientPlayer()"),
		"blockstate": "blockstate"
	}/>
}
</#if>
</#macro>

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

<#macro onEntityFallsOn procedure="">
<#if hasProcedure(data.onEntityFallsOn)>
@Override public void fallOn(Level world, BlockState blockstate, BlockPos pos, Entity entity, double distance) {
	super.fallOn(world, blockstate, pos, entity, distance);
	<@procedureCode data.onEntityFallsOn, {
		"x": "pos.getX()",
		"y": "pos.getY()",
		"z": "pos.getZ()",
		"world": "world",
		"entity": "entity",
		"blockstate": "blockstate",
		"distance": "distance"
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
@Override public InteractionResult useWithoutItem(BlockState blockstate, Level world, BlockPos pos, Player entity, BlockHitResult hit) {
	super.useWithoutItem(blockstate, world, pos, entity, hit);
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
@Override public boolean isValidBonemealTarget(LevelReader worldIn, BlockPos pos, BlockState blockstate) {
	<#if hasProcedure(isBonemealTargetCondition)>
	if (worldIn instanceof LevelAccessor world) {
		return <@procedureCode isBonemealTargetCondition, {
			"x": "pos.getX()",
			"y": "pos.getY()",
			"z": "pos.getZ()",
			"world": "world",
			"blockstate": "blockstate",
			"clientSide": "world.isClientSide()"
		}/>
	}
	return false;
	<#else>
	return true;
	</#if>
}

@Override public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState blockstate) {
	<#if hasProcedure(bonemealSuccessCondition)>
	return <@procedureCode bonemealSuccessCondition, {
		"x": "pos.getX()",
		"y": "pos.getY()",
		"z": "pos.getZ()",
		"world": "world",
		"blockstate": "blockstate"
	}/>
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