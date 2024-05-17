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
package ${package}.item.inventory;

<#compress>
@EventBusSubscriber(Dist.CLIENT) public class ${name}InventoryCapability extends ItemStackHandler {

	public static final Codec<${name}InventoryCapability> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(${name}InventoryCapability::new, cap -> cap.stacks);

	public static final StreamCodec<RegistryFriendlyByteBuf, ${name}InventoryCapability> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC
			.apply(ByteBufCodecs.list()).map(${name}InventoryCapability::new, cap -> cap.stacks);

	@SubscribeEvent @OnlyIn(Dist.CLIENT) public static void onItemDropped(ItemTossEvent event) {
		if (event.getEntity().getItem().getItem() == ${JavaModName}Items.${data.getModElement().getRegistryNameUpper()}.get()) {
			if (Minecraft.getInstance().screen instanceof ${data.guiBoundTo}Screen) {
				Minecraft.getInstance().player.closeContainer();
			}
		}
	}

	${name}InventoryCapability(List<ItemStack> stackList) {
		this();
		for (int i = 0; i < stackList.size(); i++) {
			this.stacks.set(i, stackList.get(i));
		}
	}

	private ItemStack owner;

	public ${name}InventoryCapability() {
		super(${data.inventorySize});
	}

	public void setOwner(ItemStack owner) {
		this.owner = owner;
	}

	@Override public int getSlotLimit(int slot) {
		return ${data.inventoryStackSize};
	}

	@Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return stack.getItem() != ${JavaModName}Items.${data.getModElement().getRegistryNameUpper()}.get();
	}

	@Override public void setSize(int size) {
	}

	@Override public ItemStack getStackInSlot(int slot) {
		return super.getStackInSlot(slot).copy();
	}

	@Override protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		if (owner != null) {
			owner.remove(${JavaModName}Items.${data.getModElement().getRegistryNameUpper()}_INVENTORY);
			owner.set(${JavaModName}Items.${data.getModElement().getRegistryNameUpper()}_INVENTORY, this);
		}
	}

	@Override public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return object instanceof ${name}InventoryCapability other && ItemStack.listMatches(this.stacks, other.stacks);
		}
	}

	@Override public int hashCode() {
		return ItemStack.hashStackList(this.stacks);
	}

}
</#compress>

<#-- @formatter:on -->