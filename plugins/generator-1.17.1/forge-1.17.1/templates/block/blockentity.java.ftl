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
public class CustomTileEntity extends LockableLootTileEntity implements ISidedInventory {

		<#if data.hasInventory>
	private static class TileEntityRegisterHandler {
		@SubscribeEvent public void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> event) {
			event.getRegistry().register(TileEntityType.Builder.create(CustomTileEntity::new, block).build(null).setRegistryName("${registryname}"));
		}
	}
        </#if>

<#if data.hasInventory>
	@ObjectHolder("${modid}:${registryname}")
	public static final TileEntityType<CustomTileEntity> tileEntityType = null;
</#if>

	private NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(${data.inventorySize}, ItemStack.EMPTY);

	protected CustomTileEntity() {
		super(tileEntityType);
	}

	@Override public void read(BlockState blockState, CompoundNBT compound) {
		super.read(blockState, compound);

		if (!this.checkLootAndRead(compound)) {
			this.stacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		}

		ItemStackHelper.loadAllItems(compound, this.stacks);

			<#if data.hasEnergyStorage>
			if(compound.get("energyStorage") != null)
				CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.get("energyStorage"));
            </#if>

			<#if data.isFluidTank>
			if(compound.get("fluidTank") != null)
				CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(fluidTank, null, compound.get("fluidTank"));
            </#if>
	}

	@Override public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);

		if (!this.checkLootAndWrite(compound)) {
			ItemStackHelper.saveAllItems(compound, this.stacks);
		}

           <#if data.hasEnergyStorage>
		   compound.put("energyStorage", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
           </#if>

           <#if data.isFluidTank>
		   compound.put("fluidTank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(fluidTank, null));
           </#if>

		return compound;
	}

	@Override public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
	}

	@Override public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.read(this.getBlockState(), pkt.getNbtCompound());
	}

	@Override public int getSizeInventory() {
		return stacks.size();
	}

	@Override public boolean isEmpty() {
		for (ItemStack itemstack : this.stacks)
			if (!itemstack.isEmpty())
				return false;
		return true;
	}

	@Override public ITextComponent getDefaultName() {
		return new StringTextComponent("${registryname}");
	}

	@Override public int getInventoryStackLimit() {
		return ${data.inventoryStackSize};
	}

	@Override public Container createMenu(int id, PlayerInventory player) {
			<#if !data.guiBoundTo?has_content || data.guiBoundTo == "<NONE>" || !(data.guiBoundTo)?has_content>
				return ChestContainer.createGeneric9X3(id, player, this);
            <#else>
				return new ${(data.guiBoundTo)}Gui.GuiContainerMod(id, player, new PacketBuffer(Unpooled.buffer()).writeBlockPos(this.getPos()));
            </#if>
	}

	@Override public ITextComponent getDisplayName() {
		return new StringTextComponent("${data.name}");
	}

	@Override protected NonNullList<ItemStack> getItems() {
		return this.stacks;
	}

	@Override protected void setItems(NonNullList<ItemStack> stacks) {
		this.stacks = stacks;
	}

	@Override public boolean isItemValidForSlot(int index, ItemStack stack) {
			<#list data.inventoryOutSlotIDs as id>
			    if (index == ${id})
					return false;
            </#list>
		return true;
	}

<#-- START: ISidedInventory -->
	@Override public int[] getSlotsForFace(Direction side) {
		return IntStream.range(0, this.getSizeInventory()).toArray();
	}

	@Override public boolean canInsertItem(int index, ItemStack stack, @Nullable Direction direction) {
		return this.isItemValidForSlot(index, stack);
	}

	@Override public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
			<#list data.inventoryInSlotIDs as id>
			    if (index == ${id})
					return false;
            </#list>
		return true;
	}
<#-- END: ISidedInventory -->

	private final LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());

		<#if data.hasEnergyStorage>
		private final EnergyStorage energyStorage = new EnergyStorage(${data.energyCapacity}, ${data.energyMaxReceive}, ${data.energyMaxExtract}, ${data.energyInitial}) {
			@Override public int receiveEnergy(int maxReceive, boolean simulate) {
				int retval = super.receiveEnergy(maxReceive, simulate);
				if(!simulate) {
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
				}
				return retval;
			}

			@Override public int extractEnergy(int maxExtract, boolean simulate) {
				int retval = super.extractEnergy(maxExtract, simulate);
				if(!simulate) {
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
				}
				return retval;
			}
		};
        </#if>

		<#if data.isFluidTank>
            <#if data.fluidRestrictions?has_content>
			private final FluidTank fluidTank = new FluidTank(${data.fluidCapacity}, fs -> {
				<#list data.fluidRestrictions as fluidRestriction>
                    <#if fluidRestriction.getUnmappedValue().startsWith("CUSTOM:")>
                        <#if fluidRestriction.getUnmappedValue().endsWith(":Flowing")>
						if(fs.getFluid() == ${(fluidRestriction.getUnmappedValue().replace("CUSTOM:", "").replace(":Flowing", ""))}Block.flowing) return true;
                        <#else>
						if(fs.getFluid() == ${(fluidRestriction.getUnmappedValue().replace("CUSTOM:", ""))}Block.still) return true;
                        </#if>
                    <#else>
					if(fs.getFluid() == Fluids.${fluidRestriction}) return true;
                    </#if>
                </#list>

				return false;
			}) {
				@Override protected void onContentsChanged() {
					super.onContentsChanged();
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
				}
			};
            <#else>
			private final FluidTank fluidTank = new FluidTank(${data.fluidCapacity}) {
				@Override protected void onContentsChanged() {
					super.onContentsChanged();
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
				}
			};
            </#if>
        </#if>

	@Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (!this.removed && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return handlers[facing.ordinal()].cast();

			<#if data.hasEnergyStorage>
			if (!this.removed && capability == CapabilityEnergy.ENERGY)
				return LazyOptional.of(() -> energyStorage).cast();
            </#if>

			<#if data.isFluidTank>
			if (!this.removed && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
				return LazyOptional.of(() -> fluidTank).cast();
            </#if>

		return super.getCapability(capability, facing);
	}

	@Override public void remove() {
		super.remove();
		for(LazyOptional<? extends IItemHandler> handler : handlers)
			handler.invalidate();
	}

}
<#-- @formatter:on -->