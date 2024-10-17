<#-- @formatter:off -->
<#include "../procedures.java.ftl">

package ${package}.item;

public class Item${name} extends Item {

	@GameRegistry.ObjectHolder("${modid}:${registryname}")
	public static final Item block = null;

	public Item${name}() {
		super();
		this.maxStackSize = 1;
		setMaxDamage(64);
		setCreativeTab(${data.igniterTab});
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer entity, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		pos = pos.offset(facing);
		ItemStack itemstack = entity.getHeldItem(hand);
		if (!entity.canPlayerEdit(pos, facing, itemstack)) {
			return EnumActionResult.FAIL;
		} else {
			if (world.isAirBlock(pos))
				World${name}.portal.portalSpawn(world, pos);

			<#if hasProcedure(data.whenPortaTriggerlUsed)>
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				<@procedureOBJToCode data.whenPortaTriggerlUsed/>
			</#if>

			itemstack.damageItem(1, entity);
			return EnumActionResult.SUCCESS;
		}
	}
}

<#-- @formatter:on -->