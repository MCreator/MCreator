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
<#include "mcitems.ftl">
<#include "procedures.java.ftl">

package ${package}.item;

@${JavaModName}Elements.ModElement.Tag
public class ${name}Item extends ${JavaModName}Elements.ModElement{

	@ObjectHolder("${modid}:${registryname}")
	public static final Item block = null;

	public ${name}Item (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.items.add(() ->
		<#if data.toolType == "Pickaxe" || data.toolType == "Axe" || data.toolType == "Sword" || data.toolType == "Spade" || data.toolType == "Hoe">
			new ${data.toolType.toString().replace("Spade", "Shovel")}Item(new IItemTier() {
				public int getMaxUses() {
					return ${data.usageCount};
				}

   				public float getEfficiency() {
					return ${data.efficiency}f;
				}

   				public float getAttackDamage() {
					return ${data.damageVsEntity - 2}f;
				}

   				public int getHarvestLevel() {
					return ${data.harvestLevel};
				}

   				public int getEnchantability() {
					return ${data.enchantability};
				}

   				public Ingredient getRepairMaterial() {
					<#if data.repairItems?has_content>
					return Ingredient.fromStacks(
							<#list data.repairItems as repairItem>
							${mappedMCItemToItemStackCode(repairItem,1)}<#if repairItem?has_next>,</#if>
                			</#list>
							);
					<#else>
					return Ingredient.EMPTY;
					</#if>
				}
			}, <#if data.toolType=="Hoe">
					${data.attackSpeed - 4}f
				<#else>
					<#if data.toolType=="Sword">3
					<#else>1
					</#if>
					,${data.attackSpeed - 4}f
				</#if>, new Item.Properties().group(${data.creativeTab})) {
		<#elseif data.toolType=="Shears">
			new ShearsItem(new Item.Properties().group(${data.creativeTab}).maxDamage(${data.usageCount})) {
				@Override public int getItemEnchantability() {
					return ${data.enchantability};
				}

				@Override public float getDestroySpeed(ItemStack stack, BlockState block) {
					return ${data.efficiency}f;
				}
		<#else>
        	new ItemToolCustom() {
		</#if>

		<#if data.stayInGridWhenCrafting>
			@Override public boolean hasContainerItem() {
				return true;
			}

			<#if data.damageOnCrafting && data.usageCount != 0>
			@Override public ItemStack getContainerItem(ItemStack itemstack) {
				ItemStack retval = new ItemStack(this);
				retval.setDamage(itemstack.getDamage() + 1);
				if(retval.getDamage() >= retval.getMaxDamage()) {
					return ItemStack.EMPTY;
				}
				return retval;
			}
			<#else>
			@Override public ItemStack getContainerItem(ItemStack itemstack) {
				return new ItemStack(this);
			}
			</#if>
		</#if>

		<#if data.specialInfo?has_content>
		@Override public void addInformation(ItemStack itemstack, World world, List<ITextComponent> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			<#list data.specialInfo as entry>
			list.add(new StringTextComponent("${JavaConventions.escapeStringForJava(entry)}"));
			</#list>
		}
		</#if>

		<#if hasProcedure(data.onRightClickedInAir)>
		@Override public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity entity, Hand hand){
			ActionResult<ItemStack> retval = super.onItemRightClick(world,entity,hand);
			ItemStack itemstack = retval.getResult();
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
			<@procedureOBJToCode data.onRightClickedInAir/>
			return retval;
		}
		</#if>

		<#if hasProcedure(data.onRightClickedOnBlock)>
		@Override public ActionResultType onItemUse(ItemUseContext context) {
			ActionResultType retval = super.onItemUse(context);
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

		<#if hasProcedure(data.onBlockDestroyedWithTool)>
		@Override public boolean onBlockDestroyed(ItemStack itemstack, World world, BlockState bl, BlockPos pos, LivingEntity entity){
			boolean retval = super.onBlockDestroyed(itemstack,world,bl,pos,entity);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onBlockDestroyedWithTool/>
			return retval;
		}
		</#if>

		<#if hasProcedure(data.onCrafted)>
		@Override public void onCreated(ItemStack itemstack, World world, PlayerEntity entity){
			super.onCreated(itemstack,world,entity);
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
			<@procedureOBJToCode data.onCrafted/>
		}
		</#if>

		<#if hasProcedure(data.onEntityHitWith)>
		@Override public boolean hitEntity(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity){
			boolean retval = super.hitEntity(itemstack,entity,sourceentity);
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

		<#if hasProcedure(data.onStoppedUsing)>
		@Override public void onPlayerStoppedUsing(ItemStack itemstack, World world, LivingEntity entity, int time){
			super.onPlayerStoppedUsing(itemstack,world,entity,time);
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
			<@procedureOBJToCode data.onStoppedUsing/>
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

		<#if data.hasGlow>
		@Override @OnlyIn(Dist.CLIENT) public boolean hasEffect(ItemStack itemstack) {
		    <#if hasCondition(data.glowCondition)>
        	if (!(<@procedureOBJToConditionCode data.glowCondition/>)) {
        	    return false;
        	}
        	</#if>
			return true;
		}
        </#if>

		}.setRegistryName("${registryname}"));
	}

<#if data.toolType=="Special">
    private static class ItemToolCustom extends Item {

		protected ItemToolCustom() {
			super(new Item.Properties().group(${data.creativeTab}).maxDamage(${data.usageCount}));
		}

		@Override public float getDestroySpeed(ItemStack itemstack, BlockState blockstate) {
			 <#list data.blocksAffected as restrictionBlock>
                 if (blockstate.getBlock() == ${mappedBlockToBlockStateCode(restrictionBlock)}.getBlock())
                 	return ${data.efficiency}f;
             </#list>
			return 1;
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
			stack.damageItem(1, entityLiving, i -> i.sendBreakAnimation(EquipmentSlotType.MAINHAND));
			return true;
		}

		@Override public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
			stack.damageItem(2, attacker, i -> i.sendBreakAnimation(EquipmentSlotType.MAINHAND));
			return true;
		}

		@Override public int getItemEnchantability() {
			return ${data.enchantability};
		}

		@Override public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
   		   Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
   		   if (equipmentSlot == EquipmentSlotType.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", ${data.damageVsEntity - 2}f, AttributeModifier.Operation.ADDITION));
   		   		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", ${data.attackSpeed - 4}, AttributeModifier.Operation.ADDITION));
   		   }

   		   return multimap;
   		}

	}
<#elseif data.toolType=="MultiTool">
    private static class ItemToolCustom extends Item {

		protected ItemToolCustom() {
			super(new Item.Properties().group(${data.creativeTab}).maxDamage(${data.usageCount}));
		}

		@Override
		public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
			Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
			if (equipmentSlot == EquipmentSlotType.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", ${data.damageVsEntity - 2}f, AttributeModifier.Operation.ADDITION));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", ${data.attackSpeed - 4}, AttributeModifier.Operation.ADDITION));

			}
			return multimap;
		}

		@Override public boolean canHarvestBlock(BlockState state) {
			return ${data.harvestLevel} >= state.getHarvestLevel();
		}

		@Override public float getDestroySpeed(ItemStack itemstack, BlockState blockstate) {
			return ${data.efficiency}f;
		}

		@Override public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
			stack.damageItem(1, attacker, i -> i.sendBreakAnimation(EquipmentSlotType.MAINHAND));
			return true;
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
			stack.damageItem(1, entityLiving, i -> i.sendBreakAnimation(EquipmentSlotType.MAINHAND));
			return true;
		}

		@Override public int getItemEnchantability() {
			return ${data.enchantability};
		}

	}
</#if>

}
<#-- @formatter:on -->