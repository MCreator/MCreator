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

package ${package}.item;

@${JavaModName}Elements.ModElement.Tag
public class ${name}Item extends ${JavaModName}Elements.ModElement{

	@ObjectHolder("${modid}:${registryname}")
	public static final Item block = null;

	public ${name}Item (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.items.add(() -> new MusicDiscItemCustom());
	}

	public static class MusicDiscItemCustom extends MusicDiscItem {

		public MusicDiscItemCustom() {
			<#if data.music.getUnmappedValue().startsWith("CUSTOM:")>
			super(0, ${JavaModName}Elements.sounds.get(new ResourceLocation("${data.music}")),
					new Item.Properties().group(${data.creativeTab}).maxStackSize(1).rarity(Rarity.RARE));
			<#else>
			super(0, (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.music}")),
					new Item.Properties().group(${data.creativeTab}).maxStackSize(1).rarity(Rarity.RARE));
			</#if>
			setRegistryName("${registryname}");
		}

		<#if data.hasGlow>
		@Override @OnlyIn(Dist.CLIENT) public boolean hasEffect(ItemStack itemstack) {
			return true;
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

		<#if hasProcedure(data.onRightClickedInAir)>
		@Override public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity entity, Hand hand) {
			ActionResult<ItemStack> ar = super.onItemRightClick(world, entity, hand);
			ItemStack itemstack = ar.getResult();
			double x = entity.posX;
			double y = entity.posY;
			double z = entity.posZ;
			<@procedureOBJToCode data.onRightClickedInAir/>
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

		<#if hasProcedure(data.onEntityHitWith)>
		@Override public boolean hitEntity(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
			boolean retval = super.hitEntity(itemstack, entity, sourceentity);
			double x = entity.posX;
			double y = entity.posY;
			double z = entity.posZ;
			World world = entity.world;
			<@procedureOBJToCode data.onEntityHitWith/>
			return retval;
		}
		</#if>

		<#if hasProcedure(data.onEntitySwing)>
		@Override public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
			boolean retval = super.onEntitySwing(itemstack, entity);
			double x = entity.posX;
			double y = entity.posY;
			double z = entity.posZ;
			World world = entity.world;
			<@procedureOBJToCode data.onEntitySwing/>
			return retval;
		}
		</#if>

		<#if hasProcedure(data.onCrafted)>
		@Override public void onCreated(ItemStack itemstack, World world, PlayerEntity entity) {
			super.onCreated(itemstack, world, entity);
			double x = entity.posX;
			double y = entity.posY;
			double z = entity.posZ;
			<@procedureOBJToCode data.onCrafted/>
		}
		</#if>

		<#if hasProcedure(data.onStoppedUsing)>
		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, LivingEntity entity, int time) {
			double x = entity.posX;
			double y = entity.posY;
			double z = entity.posZ;
			<@procedureOBJToCode data.onStoppedUsing/>
		}
		</#if>

		<#if hasProcedure(data.onItemInUseTick) || hasProcedure(data.onItemInInventoryTick)>
		@Override public void inventoryTick(ItemStack itemstack, World world, Entity entity, int slot, boolean selected) {
			super.inventoryTick(itemstack, world, entity, slot, selected);
			double x = entity.posX;
			double y = entity.posY;
			double z = entity.posZ;
    		<#if hasProcedure(data.onItemInUseTick)>
			if (selected)
				<@procedureOBJToCode data.onItemInUseTick/>
			</#if>
    		<@procedureOBJToCode data.onItemInInventoryTick/>
		}
		</#if>
	}

}
<#-- @formatter:on -->