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
@EventBusSubscriber(Dist.CLIENT) public class ${name}InventoryCapability extends ComponentItemHandler {

	@SubscribeEvent @OnlyIn(Dist.CLIENT) public static void onItemDropped(ItemTossEvent event) {
		if (event.getEntity().getItem().getItem() == ${JavaModName}Items.${data.getModElement().getRegistryNameUpper()}.get()) {
			if (Minecraft.getInstance().screen instanceof ${data.guiBoundTo}Screen) {
				Minecraft.getInstance().player.closeContainer();
			}
		}
	}

	public ${name}InventoryCapability(MutableDataComponentHolder parent) {
		super(parent, DataComponents.CONTAINER, ${data.inventorySize});
	}

	@Override public int getSlotLimit(int slot) {
		return ${data.inventoryStackSize};
	}

	@Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return stack.getItem() != ${JavaModName}Items.${data.getModElement().getRegistryNameUpper()}.get();
	}

	@Override public ItemStack getStackInSlot(int slot) {
		return super.getStackInSlot(slot).copy();
	}

}
</#compress>

<#-- @formatter:on -->