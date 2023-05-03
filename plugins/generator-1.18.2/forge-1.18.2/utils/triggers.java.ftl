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

<#macro onDroppedByPlayer procedure="">
<#if hasProcedure(procedure)>
@Override public boolean onDroppedByPlayer(ItemStack itemstack, Player entity) {
	<@procedureCode procedure, {
		"x": "entity.getX()",
		"y": "entity.getY()",
		"z": "entity.getZ()",
		"world": "entity.level",
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
		"world": "entity.level",
		"itemstack": "itemstack"
   	}/>
	<#else>
   	return true;
	</#if>
}
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

<#macro onDestroyedByExplosion procedure="">
	<#if hasProcedure(procedure)>
@Override public void wasExploded(Level world, BlockPos pos, Explosion e) {
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
@Override public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity) {
			super.entityInside(blockstate, world, pos, entity);
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

<#macro onRedstoneOrNeighborChanged onRedstoneOn="" onRedstoneOff="" onNeighborChanged="">
	<#if hasProcedure(onRedstoneOn) || hasProcedure(onRedstoneOff) || hasProcedure(onNeighborChanged)>
@Override public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
			super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
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
@Override public void animateTick(BlockState blockstate, Level world, BlockPos pos, Random random) {
			super.animateTick(blockstate, world, pos, random);
	<@procedureCode procedure, {
	"x": "pos.getX()",
	"y": "pos.getY()",
	"z": "pos.getZ()",
	"world": "world",
	"entity": "Minecraft.getInstance().player",
	"blockstate": "blockstate"
	}/>
}
	</#if>
</#macro>

<#macro onBlockTick procedure="" scheduleTick=false tickRate=0>
	<#if hasProcedure(procedure)>
@Override public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, Random random) {
			super.tick(blockstate, world, pos, random);
	<@procedureCode procedure, {
	"x": "pos.getX()",
	"y": "pos.getY()",
	"z": "pos.getZ()",
	"world": "world",
	"blockstate": "blockstate"
	}/>
	<#if scheduleTick>
	world.scheduleTick(pos, this, ${tickRate});
	</#if>
}
	</#if>
</#macro>

<#macro onBlockRightClicked procedure="">
	<#if hasProcedure(procedure)>
@Override public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
			super.use(blockstate, world, pos, entity, hand, hit);
	<@procedureCodeWithOptResult procedure, "actionresulttype",  "InteractionResult.SUCCESS", {
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
