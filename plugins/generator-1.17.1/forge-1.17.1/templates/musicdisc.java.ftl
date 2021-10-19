<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2021, Pylo, opensource contributors
 # 
 # This program is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 3 of the License, or
 # (at your option) any later version.
 # 
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 # 
 # You should have received a copy of the GNU General Public License
 # along with this program.  If not, see <https://www.gnu.org/licenses/>.
 # 
 # Additional permission for code generator templates (*.ftl files)
 # 
 # As a special exception, you may create a larger work that contains part or 
 # all of the MCreator code generator templates (*.ftl files) and distribute 
 # that work under terms of your choice, so long as that work isn't itself a 
 # template for code generation. Alternatively, if you modify or redistribute 
 # the template itself, you may (at your option) remove this special exception, 
 # which will cause the template and the resulting code generator output files 
 # to be licensed under the GNU General Public License without this special 
 # exception.
-->

<#-- @formatter:off -->
<#include "procedures.java.ftl">

package ${package}.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;

public class ${name}Item extends RecordItem {

	public ${name}Item() {
		<#if data.music.getUnmappedValue().startsWith("CUSTOM:")>
		super(0, ${JavaModName}Sounds.REGISTRY.get(new ResourceLocation("${data.music}")),
				new Item.Properties().tab(${data.creativeTab}).stacksTo(1).rarity(Rarity.RARE));
		<#else>
		super(0, (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.music}")),
				new Item.Properties().tab(${data.creativeTab}).stacksTo(1).rarity(Rarity.RARE));
		</#if>

		setRegistryName("${registryname}");
	}

	<#if data.hasGlow>
	@Override @OnlyIn(Dist.CLIENT) public boolean isFoil(ItemStack itemstack) {
		return true;
	}
	</#if>

	<#if data.specialInfo?has_content>
	@Override public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		<#list data.specialInfo as entry>
		list.add(new TextComponent("${JavaConventions.escapeStringForJava(entry)}"));
		</#list>
	}
	</#if>

	<#if hasProcedure(data.onRightClickedInAir)>
	@Override public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		ItemStack itemstack = ar.getObject();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		<@procedureOBJToCode data.onRightClickedInAir/>
		return ar;
	}
	</#if>

	<#if hasProcedure(data.onRightClickedOnBlock)>
	@Override public InteractionResult useOn(UseOnContext context) {
		InteractionResult retval = super.useOn(context);
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player entity = context.getPlayer();
		Direction direction = context.getClickedFace();
		BlockState blockstate = world.getBlockState(pos);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		ItemStack itemstack = context.getItemInHand();
		<#if hasReturnValue(data.onRightClickedOnBlock)>
		return <@procedureOBJToInteractionResultCode data.onRightClickedOnBlock/>;
		<#else>
		<@procedureOBJToCode data.onRightClickedOnBlock/>
		return retval;
		</#if>
	}
	</#if>

	<#if hasProcedure(data.onEntityHitWith)>
	@Override public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		Level world = entity.level;
		<@procedureOBJToCode data.onEntityHitWith/>
		return retval;
	}
	</#if>

	<#if hasProcedure(data.onEntitySwing)>
	@Override public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
		boolean retval = super.onEntitySwing(itemstack, entity);
		<@procedureCode data.onEntitySwing, {
			"x": "entity.getX()",
			"y": "entity.getY()",
			"z": "entity.getZ()",
			"world": "entity.level",
			"entity": "entity"
		}/>
		return retval;
	}
	</#if>

	<#if hasProcedure(data.onCrafted)>
	@Override public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
		super.onCraftedBy(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		<@procedureOBJToCode data.onCrafted/>
	}
	</#if>

	<#if hasProcedure(data.onStoppedUsing)>
	@Override
	public void releaseUsing(ItemStack itemstack, Level world, LivingEntity entity, int time) {
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		<@procedureOBJToCode data.onStoppedUsing/>
	}
	</#if>

	<#if hasProcedure(data.onItemInUseTick) || hasProcedure(data.onItemInInventoryTick)>
	@Override public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		<#if hasProcedure(data.onItemInUseTick)>
		if (selected)
			<@procedureOBJToCode data.onItemInUseTick/>
		</#if>
		<@procedureOBJToCode data.onItemInInventoryTick/>
	}
	</#if>

}
<#-- @formatter:on -->