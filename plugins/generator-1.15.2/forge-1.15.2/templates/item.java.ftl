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

@${JavaModName}Elements.ModElement.Tag public class ${name}Item extends ${JavaModName}Elements.ModElement{

@ObjectHolder("${modid}:${registryname}")
public static final Item block = null;

public ${name}Item(${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});

<#if data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>">
		MinecraftForge.EVENT_BUS.register(this);
</#if>
		}

<#if data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>">
@SubscribeEvent @OnlyIn(Dist.CLIENT) public void onItemDropped(ItemTossEvent event) {
		if(event.getEntityItem().getItem().getItem() == block) {
		if (Minecraft.getInstance().currentScreen instanceof ${(data.guiBoundTo)}Gui.GuiWindow) {
		Minecraft.getInstance().player.closeScreen();
		}
		}
		}
</#if>

@Override public void initElements() {
		elements.items.add(() -> new ItemCustom());
		}

public static class ItemCustom extends Item {

	public ItemCustom() {
		super(new Item.Properties()
				.group(${data.creativeTab})
					<#if data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>">
					.maxStackSize(1)
				<#elseif data.damageCount != 0>
					.maxDamage(${data.damageCount})
					<#else>
					.maxStackSize(${data.stackSize})
					</#if>
					.rarity(Rarity.${data.rarity})
			);
		setRegistryName("${registryname}");
	}

		<#if data.stayInGridWhenCrafting>
	@Override public boolean hasContainerItem() {
		return true;
	}

			<#if data.damageOnCrafting && data.damageCount != 0>
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

	@Override public int getItemEnchantability() {
		return ${data.enchantability};
	}

	@Override public int getUseDuration(ItemStack itemstack) {
		return ${data.useDuration};
	}

	@Override public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
		return ${data.toolType}F;
	}

		<#if data.enableMeleeDamage>
	@Override public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot);
		if (slot == EquipmentSlotType.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "item_damage", (double) ${data.damageVsEntity - 2}, AttributeModifier.Operation.ADDITION));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "item_attack_speed", -2.4, AttributeModifier.Operation.ADDITION));
		}
		return multimap;
	}
		</#if>

		<#if data.hasGlow>
	@Override @OnlyIn(Dist.CLIENT) public boolean hasEffect(ItemStack itemstack) {
		return true;
	}
        </#if>

		<#if data.destroyAnyBlock>
	@Override public boolean canHarvestBlock(BlockState state) {
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

		<#if hasProcedure(data.onRightClickedInAir) || (data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>")>
	@Override public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity entity, Hand hand) {
		ActionResult<ItemStack> ar = super.onItemRightClick(world, entity, hand);
		ItemStack itemstack = ar.getResult();
		double x = entity.getPosX();
		double y = entity.getPosY();
		double z = entity.getPosZ();

			<#if data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>">
		if(entity instanceof ServerPlayerEntity) {
			NetworkHooks.openGui((ServerPlayerEntity) entity, new INamedContainerProvider() {

				@Override public ITextComponent getDisplayName() {
					return new StringTextComponent("${data.name}");
				}

				@Override public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
					PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
					packetBuffer.writeBlockPos(new BlockPos(x, y, z));
					packetBuffer.writeByte(hand == Hand.MAIN_HAND ? 0 : 1);
					return new ${(data.guiBoundTo)}Gui.GuiContainerMod(id, inventory, packetBuffer);
				}

			}, buf -> {
				buf.writeBlockPos(new BlockPos(x, y, z));
				buf.writeByte(hand == Hand.MAIN_HAND ? 0 : 1);
			});
		}
			</#if>

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

		<#if hasProcedure(data.onStoppedUsing)>
	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, LivingEntity entity, int time) {
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

		<#if data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>">
	@Override public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT compound) {
		return new InventoryCapability();
	}

	@Override public CompoundNBT getShareTag(ItemStack stack) {
		CompoundNBT nbt = super.getShareTag(stack);
		if(nbt != null)
			stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> nbt.put("Inventory", ((ItemStackHandler) capability).serializeNBT()));
		return nbt;
	}

	@Override public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
		super.readShareTag(stack, nbt);
		if(nbt != null)
			stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> ((ItemStackHandler) capability).deserializeNBT((CompoundNBT) nbt.get("Inventory")));
	}
		</#if>

}

<#if data.guiBoundTo?has_content && data.guiBoundTo != "<NONE>">
private static class InventoryCapability implements ICapabilitySerializable<CompoundNBT> {

	private final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(this::createItemHandler);

	@Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.inventory.cast() : LazyOptional.empty();
	}

	@Override public CompoundNBT serializeNBT() {
		return getItemHandler().serializeNBT();
	}

	@Override public void deserializeNBT(CompoundNBT nbt) {
		getItemHandler().deserializeNBT(nbt);
	}

	private ItemStackHandler createItemHandler() {
		return new ItemStackHandler(${data.inventorySize}) {

			@Override public int getSlotLimit(int slot) {
				return ${data.inventoryStackSize};
			}

			@Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return stack.getItem() != block;
			}

		};
	}

	private ItemStackHandler getItemHandler() {
		return inventory.orElseThrow(RuntimeException::new);
	}

}
</#if>

		}
<#-- @formatter:on -->
