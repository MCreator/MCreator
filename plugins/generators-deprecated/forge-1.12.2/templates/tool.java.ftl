<#-- @formatter:off -->
<#include "mcitems.ftl">
<#include "procedures.java.ftl">

package ${package}.item;

@Elements${JavaModName}.ModElement.Tag public class Item${name} extends Elements${JavaModName}.ModElement {

	@GameRegistry.ObjectHolder("${modid}:${registryname}")
	public static final Item block = null;

	public Item${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.items.add(() ->
		<#if data.toolType!="Special" && data.toolType!="MultiTool" && data.toolType!="Axe" && data.toolType!="Shears">
			new Item${data.toolType}(EnumHelper.addToolMaterial(
					"${registryname?upper_case}", ${data.harvestLevel}, ${data.usageCount}, ${data.efficiency}f,
			<#if data.toolType=="Hoe">${data.attackSpeed - 1}f<#else>${data.damageVsEntity - 4}f</#if>,
				${data.enchantability})){

				<#if data.toolType=="Sword">
					@Override public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        				Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
    				    if (slot == EntityEquipmentSlot.MAINHAND) {
    				        multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.getAttackDamage(), 0));
    				        multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ${data.attackSpeed - 4}, 0));
    				    }
    				    return multimap;
    				}
    			<#elseif data.toolType=="Hoe">
				<#else>
					{
						this.attackSpeed = ${data.attackSpeed - 4}f;
					}
				</#if>

				public Set<String> getToolClasses(ItemStack stack) {
					HashMap<String, Integer> ret = new HashMap<String, Integer>();
					ret.put("${data.toolType?lower_case}", ${data.harvestLevel});
					return ret.keySet();
				}
		<#elseif data.toolType=="Shears">
			new ItemShears() {
				{
					setMaxDamage(${data.usageCount});
				}

				@Override public int getItemEnchantability() {
					return ${data.enchantability};
				}

				@Override public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) {
					return ${data.efficiency}f;
				}
		<#else>
        	new ItemToolCustom(){
		</#if>

		<#if hasProcedure(data.onRightClickedInAir)>
		@Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer entity,EnumHand hand){
			ActionResult<ItemStack> retval=super.onItemRightClick(world,entity,hand);
			ItemStack itemstack=retval.getResult();
			int x=(int)entity.posX;
			int y=(int)entity.posY;
			int z=(int)entity.posZ;
			<@procedureOBJToCode data.onRightClickedInAir/>
			return retval;
		}
		</#if>

		<#if hasProcedure(data.onRightClickedOnBlock)>
		@Override public EnumActionResult onItemUse(EntityPlayer entity,World world,BlockPos pos,EnumHand hand,EnumFacing direction,float hitX,float hitY,float hitZ){
			EnumActionResult retval=super.onItemUse(entity,world,pos,hand,direction,hitX,hitY,hitZ);
			int x=pos.getX();
			int y=pos.getY();
			int z=pos.getZ();
			ItemStack itemstack=entity.getHeldItem(hand);
			<@procedureOBJToCode data.onRightClickedOnBlock/>
			return retval;
		}
		</#if>

		<#if hasProcedure(data.onBlockDestroyedWithTool)>
		@Override public boolean onBlockDestroyed(ItemStack itemstack,World world,IBlockState bl,BlockPos pos,EntityLivingBase entity){
			boolean retval=super.onBlockDestroyed(itemstack,world,bl,pos,entity);
			int x=pos.getX();
			int y=pos.getY();
			int z=pos.getZ();
			<@procedureOBJToCode data.onBlockDestroyedWithTool/>
			return retval;
		}
		</#if>

		<#if hasProcedure(data.onCrafted)>
		@Override public void onCreated(ItemStack itemstack,World world,EntityPlayer entity){
			super.onCreated(itemstack,world,entity);
			int x=(int)entity.posX;
			int y=(int)entity.posY;
			int z=(int)entity.posZ;
			<@procedureOBJToCode data.onCrafted/>
		}
		</#if>

		<#if hasProcedure(data.onEntityHitWith)>
		@Override public boolean hitEntity(ItemStack itemstack,EntityLivingBase entity,EntityLivingBase entity2){
			super.hitEntity(itemstack,entity,entity2);
			int x=(int)entity.posX;
			int y=(int)entity.posY;
			int z=(int)entity.posZ;
			World world=entity.world;
			<@procedureOBJToCode data.onEntityHitWith/>
			return true;
		}
		</#if>

		<#if hasProcedure(data.onStoppedUsing)>
		@Override public void onPlayerStoppedUsing(ItemStack itemstack,World world,EntityLivingBase entity,int time){
			super.onPlayerStoppedUsing(itemstack,world,entity,time);
			int x=(int)entity.posX;
			int y=(int)entity.posY;
			int z=(int)entity.posZ;
			<@procedureOBJToCode data.onStoppedUsing/>
		}
		</#if>

		<#if hasProcedure(data.onItemInUseTick) || hasProcedure(data.onItemInInventoryTick)>
		@Override public void onUpdate(ItemStack itemstack, World world, Entity entity, int slot, boolean par5) {
			super.onUpdate(itemstack, world, entity, slot, par5);
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
    		<#if hasProcedure(data.onItemInUseTick)>
			if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHeldItemMainhand().equals(itemstack))
    	    <@procedureOBJToCode data.onItemInUseTick/>
    		</#if>
    		<@procedureOBJToCode data.onItemInInventoryTick/>
		}
		</#if>

		<#if data.hasGlow>
		@Override @SideOnly(Side.CLIENT) public boolean hasEffect(ItemStack itemstack) {
			return true;
		}
        </#if>

		}.setUnlocalizedName("${registryname}").setRegistryName("${registryname}").setCreativeTab(${data.creativeTab}));
	}

	@SideOnly(Side.CLIENT) @Override public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block,0,new ModelResourceLocation("${modid}:${registryname}" ,"inventory"));
	}

<#if data.toolType=="Special">
    private static class ItemToolCustom extends Item {

		protected ItemToolCustom() {
			setMaxDamage(${data.usageCount});
			setMaxStackSize(1);
		}

		@Override public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
			if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", ${data.damageVsEntity - 4}f, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", ${data.attackSpeed - 4}, 0));
			}
			return multimap;
		}

		@Override public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) {
			IBlockState require;
			 <#list data.blocksAffected as restrictionBlock>
                    require = ${mappedBlockToBlockStateCode(restrictionBlock)};
                 <#if hasMetadata(restrictionBlock)>
			        try {
						if ((par2Block.getBlock() == require.getBlock()) && (
								par2Block.getBlock().getMetaFromState(par2Block) == require.getBlock()
										.getMetaFromState(require)))
							return ${data.efficiency}f;
					} catch (Exception e) {
						if (par2Block.getBlock() == require.getBlock())
							return ${data.efficiency}f;
					}
                 <#else>
                    if (par2Block.getBlock() == require.getBlock())
						return ${data.efficiency}f;
                 </#if>
             </#list>
			return 0;
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
				EntityLivingBase entityLiving) {
			stack.damageItem(1, entityLiving);
			return true;
		}

		@Override public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			stack.damageItem(2, attacker);
			return true;
		}

		@Override public boolean isFull3D() {
			return true;
		}

		@Override public int getItemEnchantability() {
			return ${data.enchantability};
		}

	}
<#elseif data.toolType=="MultiTool">
    private static class ItemToolCustom extends Item {

		protected ItemToolCustom() {
			setMaxDamage(${data.usageCount});
			setMaxStackSize(1);
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
			if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", ${data.damageVsEntity - 4}f, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", ${data.attackSpeed - 4}, 0));
			}
			return multimap;
		}

		@Override public boolean canHarvestBlock(IBlockState blockIn) {
			return true;
		}

		@Override public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) {
			return ${data.efficiency}f;
		}

		@Override public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			stack.damageItem(1, attacker);
			return true;
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
				EntityLivingBase entityLiving) {
			stack.damageItem(1, entityLiving);
			return true;
		}

		@Override public boolean isFull3D() {
			return true;
		}

		@Override public int getItemEnchantability() {
			return ${data.enchantability};
		}

	}
<#elseif data.toolType=="Axe">
    private static class ItemToolCustom extends ItemTool {

		private static final Set<Block> effective_items_set = com.google.common.collect.Sets.newHashSet(
				new Block[] { Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN,
						Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON,
						Blocks.WOODEN_PRESSURE_PLATE });

		protected ItemToolCustom() {
			super(EnumHelper.addToolMaterial(
					"${registryname?upper_case}", ${data.harvestLevel}, ${data.usageCount}, ${data.efficiency}f, ${data.damageVsEntity - 4}f, ${data.enchantability}),
					effective_items_set); this.attackDamage = ${data.damageVsEntity - 4}f;

			this.attackSpeed = ${data.attackSpeed - 4}f;
		}

		@Override public float getDestroySpeed(ItemStack stack, IBlockState state) {
			Material material = state.getMaterial();
			return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE ?
					super.getDestroySpeed(stack, state) :
					this.efficiency;
		}

	}
</#if>

}
<#-- @formatter:on -->