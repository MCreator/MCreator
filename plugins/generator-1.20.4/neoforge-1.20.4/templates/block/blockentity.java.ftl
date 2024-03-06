<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2023, Pylo, opensource contributors
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
package ${package}.block.entity;

public class ${name}BlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {

	private NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(${data.inventorySize}, ItemStack.EMPTY);

	private final SidedInvWrapper handler = new SidedInvWrapper(this, null);

	public ${name}BlockEntity(BlockPos position, BlockState state) {
		super(${JavaModName}BlockEntities.${data.getModElement().getRegistryNameUpper()}.get(), position, state);
	}

	@Override public void load(CompoundTag compound) {
		super.load(compound);

		if (!this.tryLoadLootTable(compound))
			this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

		ContainerHelper.loadAllItems(compound, this.stacks);

		<#if data.hasEnergyStorage>
		if(compound.get("energyStorage") instanceof IntTag intTag)
			energyStorage.deserializeNBT(intTag);
		</#if>

		<#if data.isFluidTank>
		if(compound.get("fluidTank") instanceof CompoundTag compoundTag)
			fluidTank.readFromNBT(compoundTag);
		</#if>
	}

	@Override public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);

		if (!this.trySaveLootTable(compound)) {
			ContainerHelper.saveAllItems(compound, this.stacks);
		}

		<#if data.hasEnergyStorage>
		compound.put("energyStorage", energyStorage.serializeNBT());
		</#if>

		<#if data.isFluidTank>
		compound.put("fluidTank", fluidTank.writeToNBT(new CompoundTag()));
		</#if>
	}

	@Override public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override public CompoundTag getUpdateTag() {
		return this.saveWithFullMetadata();
	}

	@Override public int getContainerSize() {
		return stacks.size();
	}

	@Override public boolean isEmpty() {
		for (ItemStack itemstack : this.stacks)
			if (!itemstack.isEmpty())
				return false;
		return true;
	}

	@Override public Component getDefaultName() {
		return Component.literal("${registryname}");
	}

	@Override public int getMaxStackSize() {
		return ${data.inventoryStackSize};
	}

	@Override public AbstractContainerMenu createMenu(int id, Inventory inventory) {
		<#if !data.guiBoundTo?has_content || data.guiBoundTo == "<NONE>" || !(data.guiBoundTo)?has_content>
		return ChestMenu.threeRows(id, inventory);
		<#else>
		return new ${data.guiBoundTo}Menu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
		</#if>
	}

	@Override public Component getDisplayName() {
		return Component.literal("${data.name}");
	}

	@Override protected NonNullList<ItemStack> getItems() {
		return this.stacks;
	}

	@Override protected void setItems(NonNullList<ItemStack> stacks) {
		this.stacks = stacks;
	}

	@Override public boolean canPlaceItem(int index, ItemStack stack) {
		<#list data.inventoryOutSlotIDs as id>
		if (index == ${id})
			return false;
		</#list>
		return true;
	}

	<#-- START: WorldlyContainer -->
	@Override public int[] getSlotsForFace(Direction side) {
		return IntStream.range(0, this.getContainerSize()).toArray();
	}

	@Override public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
		return this.canPlaceItem(index, stack);
	}

	@Override public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		<#list data.inventoryInSlotIDs as id>
		if (index == ${id})
			return false;
        </#list>
		return true;
	}
	<#-- END: WorldlyContainer -->

	public SidedInvWrapper getItemHandler() {
		return handler;
	}

	<#if data.hasEnergyStorage>
	private final EnergyStorage energyStorage = new EnergyStorage(${data.energyCapacity}, ${data.energyMaxReceive}, ${data.energyMaxExtract}, ${data.energyInitial}) {
		@Override public int receiveEnergy(int maxReceive, boolean simulate) {
			int retval = super.receiveEnergy(maxReceive, simulate);
			if(!simulate) {
				setChanged();
				level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
			}
			return retval;
		}

		@Override public int extractEnergy(int maxExtract, boolean simulate) {
			int retval = super.extractEnergy(maxExtract, simulate);
			if(!simulate) {
				setChanged();
				level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
			}
			return retval;
		}
	};

	public EnergyStorage getEnergyStorage() {
		return energyStorage;
	}
	</#if>

	<#if data.isFluidTank>
	private final FluidTank fluidTank = new FluidTank(${data.fluidCapacity}
		<#if data.fluidRestrictions?has_content>, fs -> {
		<#list data.fluidRestrictions as fluidRestriction>
            <#if fluidRestriction.getUnmappedValue().startsWith("CUSTOM:")>
				if(fs.getFluid() == ${JavaModName}Fluids.<#if fluidRestriction.getUnmappedValue().endsWith(":Flowing")>FLOWING_</#if>${generator.getRegistryNameForModElement(fluidRestriction.getUnmappedValue()?remove_beginning("CUSTOM:")?remove_ending(":Flowing"))?upper_case}.get()) return true;
            <#else>
				if(fs.getFluid() == Fluids.${fluidRestriction}) return true;
            </#if>
        </#list>
		return false;
		}</#if>
	) {
		@Override protected void onContentsChanged() {
			super.onContentsChanged();
			setChanged();
			level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
		}
	};

	public FluidTank getFluidTank() {
		return fluidTank;
	}
    </#if>

}
<#-- @formatter:on -->