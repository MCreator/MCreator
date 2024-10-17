<#-- @formatter:off -->
<#include "procedures.java.ftl">

package ${package}.item;

@Elements${JavaModName}.ModElement.Tag public class Item${name} extends Elements${JavaModName}.ModElement{

	@GameRegistry.ObjectHolder("${modid}:${registryname}")
	public static final Item block = null;

	public Item${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.items.add(() -> new MusicDiscItemCustom());
	}

	@SideOnly(Side.CLIENT) @Override public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("${modid}:${registryname}" ,"inventory"));
	}

	public static class MusicDiscItemCustom extends ItemRecord {

		public MusicDiscItemCustom() {
			<#if data.music.getUnmappedValue().startsWith("CUSTOM:")>
			super("${registryname}", Elements${JavaModName}.sounds.get(new ResourceLocation("${data.music}")));
			<#else>
			super("${registryname}", (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("${data.music}")));
			</#if>
			setUnlocalizedName("${registryname}");
			setRegistryName("${registryname}");
			setCreativeTab(${data.creativeTab});
		}

		<#if data.hasGlow>
		@Override @SideOnly(Side.CLIENT) public boolean hasEffect(ItemStack itemstack) {
			return true;
		}
		</#if>

		<#if data.specialInfo?has_content>
		@Override public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			<#list data.specialInfo as entry>
			list.add("${JavaConventions.escapeStringForJava(entry)}" );
			</#list>
		}
		</#if>

		<#if hasProcedure(data.onRightClickedInAir)>
		@Override public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ActionResult<ItemStack> ar = super.onItemRightClick(world, entity, hand);
			ItemStack itemstack = ar.getResult();
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			<@procedureOBJToCode data.onRightClickedInAir/>
			return ar;
		}
		</#if>

		<#if hasProcedure(data.onRightClickedOnBlock)>
		@Override
		public EnumActionResult onItemUseFirst(EntityPlayer entity, World world, BlockPos pos, EnumFacing direction,
				float hitX, float hitY, float hitZ, EnumHand hand) {
			EnumActionResult retval = super.onItemUseFirst(entity, world, pos, direction, hitX, hitY, hitZ, hand);
			ItemStack itemstack = entity.getHeldItem(hand);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onRightClickedOnBlock/>
			return retval;
		}
		</#if>

		<#if hasProcedure(data.onEntityHitWith)>
		@Override public boolean hitEntity(ItemStack itemstack, EntityLivingBase entity, EntityLivingBase entity2) {
			super.hitEntity(itemstack, entity, entity2);
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			World world = entity.world;
			<@procedureOBJToCode data.onEntityHitWith/>
			return true;
		}
		</#if>

		<#if hasProcedure(data.onCrafted)>
		@Override public void onCreated(ItemStack itemstack, World world, EntityPlayer entity) {
			super.onCreated(itemstack, world, entity);
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			<@procedureOBJToCode data.onCrafted/>
		}
		</#if>

		<#if hasProcedure(data.onStoppedUsing)>
		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entity, int time) {
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
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
			if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHeldItemMainhand()
					.equals(itemstack))
				<@procedureOBJToCode data.onItemInUseTick/>
			</#if>
			<@procedureOBJToCode data.onItemInInventoryTick/>
		}
		</#if>
	}

}
<#-- @formatter:on -->