<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
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
<#include "mcitems.ftl">

package ${package}.item;

@${JavaModName}Elements.ModElement.Tag
public class ${name}Item extends ${JavaModName}Elements.ModElement{

	@ObjectHolder("${modid}:${registryname}")
	public static final Item block = null;

	public ${name}Item (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.items.add(() -> new FoodItemCustom());
	}

	public static class FoodItemCustom extends Item {

		public FoodItemCustom() {
			super(new Item.Properties().group(${data.creativeTab}).maxStackSize(${data.stackSize}).rarity(Rarity.${data.rarity})
				.food((new Food.Builder()).hunger(${data.nutritionalValue}).saturation(${data.saturation}f)
				<#if data.isAlwaysEdible>.setAlwaysEdible()</#if>
				<#if data.forDogs>.meat()</#if>
				.build()
			));
			setRegistryName("${registryname}");
		}

		<#if data.eatingSpeed != 32>
		@Override public int getUseDuration(ItemStack stack) {
			return ${data.eatingSpeed};
		}
        </#if>

		<#if data.hasGlow>
		@Override @OnlyIn(Dist.CLIENT) public boolean hasEffect(ItemStack itemstack) {
		    <#if hasCondition(data.glowCondition)>
			PlayerEntity entity = Minecraft.getInstance().player;
			World world = entity.world;
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
        	if (!(<@procedureOBJToConditionCode data.glowCondition/>)) {
        	    return false;
        	}
        	</#if>
			return true;
		}
        </#if>

		@Override public UseAction getUseAction(ItemStack itemstack) {
			return UseAction.${data.animation?upper_case};
		}

		<#if data.animation == "drink">
		@Override public net.minecraft.util.SoundEvent getEatSound() {
			return net.minecraft.util.SoundEvents.ENTITY_GENERIC_DRINK;
		}
		</#if>

		<#if data.specialInfo?has_content>
		@Override public void addInformation(ItemStack itemstack, World world, List<ITextComponent> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			<#list data.specialInfo as entry>
			list.add(new StringTextComponent("${JavaConventions.escapeStringForJava(entry)}"));
			</#list>
		}
		</#if>

		<#if hasProcedure(data.onRightClicked)>
		@Override public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity entity, Hand hand) {
			ActionResult<ItemStack> ar = super.onItemRightClick(world, entity, hand);
			ItemStack itemstack = ar.getResult();
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
			<@procedureOBJToCode data.onRightClicked/>
			return ar;
		}
        </#if>

		<#if hasProcedure(data.onRightClickedOnBlock)>
		@Override public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
			ActionResultType retval = super.onItemUseFirst(stack, context);
			World world = context.getWorld();
      		BlockPos pos = context.getPos();
      		PlayerEntity entity = context.getPlayer();
      		Direction direction = context.getFace();
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			ItemStack itemstack = context.getItem();
			<@procedureOBJToCode data.onRightClickedOnBlock/>
			return retval;
		}
        </#if>

		<#if hasProcedure(data.onEaten) || (data.resultItem?? && !data.resultItem.isEmpty())>
		@Override public ItemStack onItemUseFinish(ItemStack itemstack, World world, LivingEntity entity) {
			ItemStack retval =
				<#if data.resultItem?? && !data.resultItem.isEmpty()>
					${mappedMCItemToItemStackCode(data.resultItem, 1)};
				</#if>
			super.onItemUseFinish(itemstack, world, entity);

			<#if hasProcedure(data.onEaten)>
				double x = entity.getPosX();
				double y = entity.getPosY();
				double z = entity.getPosZ();
				<@procedureOBJToCode data.onEaten/>
			</#if>

			<#if data.resultItem?? && !data.resultItem.isEmpty()>
				if (itemstack.isEmpty()) {
					return retval;
				} else {
					if (entity instanceof PlayerEntity) {
						PlayerEntity player = (PlayerEntity) entity;
						if (!player.isCreative() && !player.inventory.addItemStackToInventory(retval))
							player.dropItem(retval, false);
					}
					return itemstack;
				}
			<#else>
				return retval;
			</#if>
		}
        </#if>

		<#if hasProcedure(data.onEntityHitWith)>
		@Override public boolean hitEntity(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
			boolean retval = super.hitEntity(itemstack, entity, sourceentity);
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
			World world = entity.world;
			<@procedureOBJToCode data.onEntityHitWith/>
			return retval;
		}
        </#if>

		<#if hasProcedure(data.onEntitySwing)>
		@Override public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
			boolean retval = super.onEntitySwing(itemstack, entity);
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
			World world = entity.world;
			<@procedureOBJToCode data.onEntitySwing/>
			return retval;
		}
		</#if>

		<#if hasProcedure(data.onCrafted)>
		@Override public void onCreated(ItemStack itemstack, World world, PlayerEntity entity) {
			super.onCreated(itemstack, world, entity);
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
			<@procedureOBJToCode data.onCrafted/>
		}
        </#if>

		<#if hasProcedure(data.onItemInUseTick) || hasProcedure(data.onItemInInventoryTick)>
		@Override public void inventoryTick(ItemStack itemstack, World world, Entity entity, int slot, boolean selected) {
			super.inventoryTick(itemstack, world, entity, slot, selected);
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
    		<#if hasProcedure(data.onItemInUseTick)>
			if (selected)
    	    <@procedureOBJToCode data.onItemInUseTick/>
    		</#if>
    		<@procedureOBJToCode data.onItemInInventoryTick/>
		}
		</#if>

		<#if hasProcedure(data.onDroppedByPlayer)>
        @Override public boolean onDroppedByPlayer(ItemStack itemstack, PlayerEntity entity) {
            double x = entity.getPosX();
            double y = entity.getPosY();
            double z = entity.getPosZ();
            World world = entity.world;
            <@procedureOBJToCode data.onDroppedByPlayer/>
            return true;
        }
        </#if>
	}

}
<#-- @formatter:on -->