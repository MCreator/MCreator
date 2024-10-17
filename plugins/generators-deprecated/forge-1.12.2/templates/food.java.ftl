<#-- @formatter:off -->
<#include "procedures.java.ftl">

package ${package}.item;

@Elements${JavaModName}.ModElement.Tag public class Item${name} extends Elements${JavaModName}.ModElement {

	@GameRegistry.ObjectHolder("${modid}:${registryname}")
	public static final Item block = null;

	public Item${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT) @Override public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("${modid}:${registryname}","inventory"));
	}

	public static class ItemFoodCustom extends ItemFood {

		public ItemFoodCustom() {
			super(${data.nutritionalValue}, ${data.saturation}f, ${data.forDogs});
			setUnlocalizedName("${registryname}");
			setRegistryName("${registryname}");
			<#if data.isAlwaysEdible>setAlwaysEdible();</#if>
			setCreativeTab(${data.creativeTab});
			setMaxStackSize(${data.stackSize});
		}

		<#if data.eatingSpeed != 32>
		@Override public int getMaxItemUseDuration(ItemStack stack) {
			return ${data.eatingSpeed};
		}
        </#if>

		<#if data.hasGlow>
		@Override @SideOnly(Side.CLIENT) public boolean hasEffect(ItemStack itemstack) {
			return true;
		}
        </#if>

		@Override public EnumAction getItemUseAction(ItemStack par1ItemStack) {
			return EnumAction.${data.animation?upper_case};
		}

		<#if hasProcedure(data.onRightClicked)>
		@Override public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ActionResult<ItemStack> ar = super.onItemRightClick(world, entity, hand);
			ItemStack itemstack = ar.getResult();
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			<@procedureOBJToCode data.onRightClicked/>
			return ar;
		}
        </#if>

		<#if hasProcedure(data.onEaten)>
		@Override protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer entity) {
			super.onFoodEaten(itemStack, world, entity);
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			<@procedureOBJToCode data.onEaten/>
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
	}

}
<#-- @formatter:on -->