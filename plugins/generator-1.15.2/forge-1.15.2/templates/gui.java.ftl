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
<#include "mcitems.ftl">
<#include "procedures.java.ftl">
<#include "tokens.ftl">

package ${package}.gui;

import ${package}.${JavaModName};

@${JavaModName}Elements.ModElement.Tag public class ${name}Gui extends ${JavaModName}Elements.ModElement{

	public static HashMap guistate = new HashMap();

    <#assign mx = data.W - data.width>
    <#assign my = data.H - data.height>

	<#assign slotnum = 0>

	private static ContainerType<GuiContainerMod> containerType = null;

	public ${name}Gui(${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});

		elements.addNetworkMessage(ButtonPressedMessage.class, ButtonPressedMessage::buffer, ButtonPressedMessage::new, ButtonPressedMessage::handler);
		elements.addNetworkMessage(GUISlotChangedMessage.class, GUISlotChangedMessage::buffer, GUISlotChangedMessage::new, GUISlotChangedMessage::handler);

		containerType = new ContainerType<>(new GuiContainerModFactory());

		FMLJavaModLoadingContext.get().getModEventBus().register(this);

		<#if hasProcedure(data.onTick)>
		MinecraftForge.EVENT_BUS.register(this);
		</#if>
	}

	@OnlyIn(Dist.CLIENT) public void initElements() {
		DeferredWorkQueue.runLater(() -> ScreenManager.registerFactory(containerType, GuiWindow::new));
	}

	<#if hasProcedure(data.onTick)>
		@SubscribeEvent public void onPlayerTick(TickEvent.PlayerTickEvent event) {
			PlayerEntity entity = event.player;
			if(event.phase == TickEvent.Phase.END && entity.openContainer instanceof GuiContainerMod) {
				World world = entity.world;
				double x = entity.getPosX();
				double y = entity.getPosY();
				double z = entity.getPosZ();
				<@procedureOBJToCode data.onTick/>
			}
		}
	</#if>

	@SubscribeEvent public void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
		event.getRegistry().register(containerType.setRegistryName("${registryname}"));
	}

	public static class GuiContainerModFactory implements IContainerFactory {

		public GuiContainerMod create(int id, PlayerInventory inv, PacketBuffer extraData) {
			return new GuiContainerMod(id, inv, extraData);
		}

	}

	public static class GuiContainerMod extends Container implements Supplier<Map<Integer, Slot>> {

		private World world;
		private PlayerEntity entity;
		private int x, y, z;

		private IItemHandler internal;

		private Map<Integer, Slot> customSlots = new HashMap<>();

		private boolean bound = false;

		public GuiContainerMod(int id, PlayerInventory inv, PacketBuffer extraData) {
			super(containerType, id);

			this.entity = inv.player;
			this.world = inv.player.world;

			this.internal = new ItemStackHandler(${data.getMaxSlotID() + 1});

			BlockPos pos = null;
			if (extraData != null) {
				pos = extraData.readBlockPos();
				this.x = pos.getX();
				this.y = pos.getY();
				this.z = pos.getZ();
			}

			<#if data.type == 1>
				if (pos != null) {
					if (extraData.readableBytes() == 1) { // bound to item
						byte hand = extraData.readByte();
						ItemStack itemstack;
						if(hand == 0)
							itemstack = this.entity.getHeldItemMainhand();
						else
							itemstack = this.entity.getHeldItemOffhand();
						itemstack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
							this.internal = capability;
							this.bound = true;
						});
					} else if (extraData.readableBytes() > 1) {
						extraData.readByte(); // drop padding
						Entity entity = world.getEntityByID(extraData.readVarInt());
						if(entity != null)
							entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
								this.internal = capability;
								this.bound = true;
							});
					} else { // might be bound to block
						TileEntity ent = inv.player != null ? inv.player.world.getTileEntity(pos) : null;
						if (ent != null) {
							ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
								this.internal = capability;
								this.bound = true;
							});
						}
					}
				}

				<#list data.components as component>
					<#if component.getClass().getSimpleName()?ends_with("Slot")>
						<#assign slotnum += 1>
            	    this.customSlots.put(${component.id}, this.addSlot(new SlotItemHandler(internal, ${component.id},
						${(component.x - mx / 2)?int + 1},
						${(component.y - my / 2)?int + 1}) {

            	    	<#if component.disableStackInteraction>
						@Override public boolean canTakeStack(PlayerEntity player) {
							return false;
						}
            	    	</#if>

						<#if hasProcedure(component.onSlotChanged)>
            	        @Override public void onSlotChanged() {
							super.onSlotChanged();
							GuiContainerMod.this.slotChanged(${component.id}, 0, 0);
						}
						</#if>

						<#if hasProcedure(component.onTakenFromSlot)>
            	        @Override public ItemStack onTake(PlayerEntity entity, ItemStack stack) {
							ItemStack retval = super.onTake(entity, stack);
							GuiContainerMod.this.slotChanged(${component.id}, 1, 0);
							return retval;
						}
						</#if>

						<#if hasProcedure(component.onStackTransfer)>
            	        @Override public void onSlotChange(ItemStack a, ItemStack b) {
							super.onSlotChange(a, b);
							GuiContainerMod.this.slotChanged(${component.id}, 2, b.getCount() - a.getCount());
						}
						</#if>

						<#if component.disableStackInteraction>
							@Override public boolean isItemValid(ItemStack stack) {
								return false;
							}
            	        <#elseif component.getClass().getSimpleName() == "InputSlot">
							<#if component.inputLimit.toString()?has_content>
            	             @Override public boolean isItemValid(ItemStack stack) {
								 return (${mappedMCItemToItemStackCode(component.inputLimit,1)}.getItem() == stack.getItem());
							 }
							</#if>
						<#elseif component.getClass().getSimpleName() == "OutputSlot">
            	            @Override public boolean isItemValid(ItemStack stack) {
								return false;
							}
						</#if>
					}));
					</#if>
				</#list>

				<#assign coffx = ((data.width - 176) / 2 + data.inventoryOffsetX)?int>
				<#assign coffy = ((data.height - 166) / 2 + data.inventoryOffsetY)?int>

            	int si;
				int sj;

				for (si = 0; si < 3; ++si)
					for (sj = 0; sj < 9; ++sj)
						this.addSlot(new Slot(inv, sj + (si + 1) * 9, ${coffx} + 8 + sj * 18, ${coffy}+ 84 + si * 18));

				for (si = 0; si < 9; ++si)
					this.addSlot(new Slot(inv, si, ${coffx} + 8 + si * 18, ${coffy} + 142));
			</#if>

			<#if hasProcedure(data.onOpen)>
				<@procedureOBJToCode data.onOpen/>
			</#if>
		}

		public Map<Integer, Slot> get() {
			return customSlots;
		}

		@Override public boolean canInteractWith(PlayerEntity player) {
			return true;
		}

		<#if data.type == 1>
		@Override public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
			ItemStack itemstack = ItemStack.EMPTY;
			Slot slot = (Slot) this.inventorySlots.get(index);

			if (slot != null && slot.getHasStack()) {
				ItemStack itemstack1 = slot.getStack();
				itemstack = itemstack1.copy();

				if (index < ${slotnum}) {
					if (!this.mergeItemStack(itemstack1, ${slotnum}, this.inventorySlots.size(), true)) {
						return ItemStack.EMPTY;
					}
					slot.onSlotChange(itemstack1, itemstack);
				} else if (!this.mergeItemStack(itemstack1, 0, ${slotnum}, false)) {
					if (index < ${slotnum} + 27) {
						if (!this.mergeItemStack(itemstack1, ${slotnum} + 27, this.inventorySlots.size(), true)) {
							return ItemStack.EMPTY;
						}
					} else {
						if (!this.mergeItemStack(itemstack1, ${slotnum}, ${slotnum} + 27, false)) {
							return ItemStack.EMPTY;
						}
					}
					return ItemStack.EMPTY;
				}

				if (itemstack1.getCount() == 0) {
					slot.putStack(ItemStack.EMPTY);
				} else {
					slot.onSlotChanged();
				}

				if (itemstack1.getCount() == itemstack.getCount()) {
					return ItemStack.EMPTY;
				}

				slot.onTake(playerIn, itemstack1);
			}
			return itemstack;
		}

		<#-- #47997 -->
		@Override ${mcc.getMethod("net.minecraft.inventory.container.Container", "mergeItemStack", "ItemStack", "int", "int", "boolean")
			.replace("slot.onSlotChanged();", "slot.putStack(itemstack);")
			.replace("!itemstack.isEmpty()", "slot.isItemValid(itemstack) && !itemstack.isEmpty()")}

		@Override public void onContainerClosed(PlayerEntity playerIn) {
			super.onContainerClosed(playerIn);

			<#if hasProcedure(data.onClosed)>
				<@procedureOBJToCode data.onClosed/>
			</#if>

			if (!bound && (playerIn instanceof ServerPlayerEntity)) {
				if (!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerIn).hasDisconnected()) {
					for(int j = 0; j < internal.getSlots(); ++j) {
						<#list data.components as component>
							<#if component.getClass().getSimpleName()?ends_with("Slot") && !component.dropItemsWhenNotBound>
								if(j == ${component.id}) continue;
							</#if>
						</#list>
						playerIn.dropItem(internal.extractItem(j, internal.getStackInSlot(j).getCount(), false), false);
					}
				} else {
					for(int i = 0; i < internal.getSlots(); ++i) {
						<#list data.components as component>
							<#if component.getClass().getSimpleName()?ends_with("Slot") && !component.dropItemsWhenNotBound>
								if(i == ${component.id}) continue;
							</#if>
						</#list>
						playerIn.inventory.placeItemBackInInventory(playerIn.world, internal.extractItem(i, internal.getStackInSlot(i).getCount(), false));
					}
				}
			}
		}

		private void slotChanged(int slotid, int ctype, int meta) {
			if(this.world != null && this.world.isRemote) {
				${JavaModName}.PACKET_HANDLER.sendToServer(new GUISlotChangedMessage(slotid, x, y, z, ctype, meta));
				handleSlotAction(entity, slotid, ctype, meta, x, y, z);
			}
		}

		</#if>
	}

	@OnlyIn(Dist.CLIENT) public static class GuiWindow extends ContainerScreen<GuiContainerMod> {

		private World world;
		private int x, y, z;
		private PlayerEntity entity;

		<#list data.components as component>
			<#if component.getClass().getSimpleName() == "TextField">
            TextFieldWidget ${component.name};
		    <#elseif component.getClass().getSimpleName() == "Checkbox">
	        CheckboxButton ${component.name};
		    </#if>
		</#list>

		public GuiWindow(GuiContainerMod container, PlayerInventory inventory, ITextComponent text) {
			super(container, inventory, text);
			this.world = container.world;
			this.x = container.x;
			this.y = container.y;
			this.z = container.z;
			this.entity = container.entity;
			this.xSize = ${data.width};
			this.ySize = ${data.height};
		}

		<#if data.doesPauseGame>
		@Override public boolean isPauseScreen() {
			return true;
		}
		</#if>

		<#if data.renderBgLayer>
		private static final ResourceLocation texture = new ResourceLocation("${modid}:textures/${registryname}.png" );
		</#if>

		@Override public void render(int mouseX, int mouseY, float partialTicks) {
			this.renderBackground();
			super.render(mouseX, mouseY, partialTicks);
			this.renderHoveredToolTip(mouseX, mouseY);

			<#list data.components as component>
				<#if component.getClass().getSimpleName() == "TextField">
					${component.name}.render(mouseX, mouseY, partialTicks);
				</#if>
			</#list>
		}

		@Override protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
			GL11.glColor4f(1, 1, 1, 1);

			<#if data.renderBgLayer>
			Minecraft.getInstance().getTextureManager().bindTexture(texture);
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;
			this.blit(k, l, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize);
			</#if>

			<#list data.components as component>
				<#if component.getClass().getSimpleName() == "Image">
					<#if hasCondition(component.displayCondition)>
					if (<@procedureOBJToConditionCode component.displayCondition/>) {
					</#if>
						Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("${modid}:textures/${component.image}"));
						this.blit(this.guiLeft + ${(component.x - mx/2)?int}, this.guiTop + ${(component.y - my/2)?int}, 0, 0,
							${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())},
							${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())});
					<#if hasCondition(component.displayCondition)>
					}
					</#if>
				</#if>
			</#list>
		}

		@Override public boolean keyPressed(int key, int b, int c) {
			if (key == 256) {
				this.minecraft.player.closeScreen();
				return true;
			}

			<#list data.components as component>
				<#if component.getClass().getSimpleName() == "TextField">
        	    if(${component.name}.isFocused())
        	    	return ${component.name}.keyPressed(key, b, c);
				</#if>
			</#list>

			return super.keyPressed(key, b, c);
		}

		@Override public void tick() {
			super.tick();
			<#list data.components as component>
				<#if component.getClass().getSimpleName() == "TextField">
					${component.name}.tick();
				</#if>
			</#list>
		}

		@Override protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
            <#list data.components as component>
				<#if component.getClass().getSimpleName() == "Label">
					<#if hasCondition(component.displayCondition)>
					if (<@procedureOBJToConditionCode component.displayCondition/>)
					</#if>
                	this.font.drawString("${translateTokens(JavaConventions.escapeStringForJava(component.text))}",
						${(component.x - mx / 2)?int}, ${(component.y - my / 2)?int}, ${component.color.getRGB()});
				</#if>
			</#list>
		}

		@Override public void removed() {
			super.removed();
			Minecraft.getInstance().keyboardListener.enableRepeatEvents(false);
		}

		@Override public void init(Minecraft minecraft, int width, int height) {
			super.init(minecraft, width, height);

			minecraft.keyboardListener.enableRepeatEvents(true);

			<#assign btid = 0>
			<#list data.components as component>
				<#if component.getClass().getSimpleName() == "TextField">
					${component.name} = new TextFieldWidget(this.font, this.guiLeft + ${(component.x - mx/2)?int}, this.guiTop + ${(component.y - my/2)?int},
															${component.width}, ${component.height}, "${component.placeholder}")
					<#if component.placeholder?has_content>
					{
						{
							setSuggestion("${component.placeholder}");
						}

						@Override public void writeText(String text) {
							super.writeText(text);

							if(getText().isEmpty())
								setSuggestion("${component.placeholder}");
							else
								setSuggestion(null);
						}

						@Override public void setCursorPosition(int pos) {
							super.setCursorPosition(pos);

							if(getText().isEmpty())
								setSuggestion("${component.placeholder}");
							else
								setSuggestion(null);
						}
					}
					</#if>;
                    guistate.put("text:${component.name}", ${component.name});
					${component.name}.setMaxStringLength(32767);
                    this.children.add(this.${component.name});
				<#elseif component.getClass().getSimpleName() == "Button">
                    this.addButton(new Button(this.guiLeft + ${(component.x - mx/2)?int}, this.guiTop + ${(component.y - my/2)?int},
						${component.width}, ${component.height}, "${component.text}", e -> {
						${JavaModName}.PACKET_HANDLER.sendToServer(new ButtonPressedMessage(${btid}, x, y, z));

						handleButtonAction(entity, ${btid}, x, y, z);
					}));
					<#assign btid +=1>
			    <#elseif component.getClass().getSimpleName() == "Checkbox">
            	    ${component.name} = new CheckboxButton(this.guiLeft + ${(component.x - mx/2)?int}, this.guiTop + ${(component.y - my/2)?int},
            	        150, 20, "${component.text}", <#if hasCondition(component.isCheckedProcedure)>
            	        <@procedureOBJToConditionCode component.isCheckedProcedure/><#else>false</#if>);
                    ${name}Gui.guistate.put("checkbox:${component.name}", ${component.name});
                    this.addButton(${component.name});
				</#if>
			</#list>
		}

	}

	public static class ButtonPressedMessage {

		int buttonID, x, y, z;

		public ButtonPressedMessage(PacketBuffer buffer) {
			this.buttonID = buffer.readInt();
			this.x = buffer.readInt();
			this.y = buffer.readInt();
			this.z = buffer.readInt();
		}

		public ButtonPressedMessage(int buttonID, int x, int y, int z) {
			this.buttonID = buttonID;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public static void buffer(ButtonPressedMessage message, PacketBuffer buffer) {
			buffer.writeInt(message.buttonID);
			buffer.writeInt(message.x);
			buffer.writeInt(message.y);
			buffer.writeInt(message.z);
		}

		public static void handler(ButtonPressedMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				PlayerEntity entity = context.getSender();
				int buttonID = message.buttonID;
				int x = message.x;
				int y = message.y;
				int z = message.z;

				handleButtonAction(entity, buttonID, x, y, z);
			});
			context.setPacketHandled(true);
		}

	}

	public static class GUISlotChangedMessage {

		int slotID, x, y, z, changeType, meta;

		public GUISlotChangedMessage(int slotID, int x, int y, int z, int changeType, int meta) {
			this.slotID = slotID;
			this.x = x;
			this.y = y;
			this.z = z;
			this.changeType = changeType;
			this.meta = meta;
		}

		public GUISlotChangedMessage(PacketBuffer buffer) {
			this.slotID = buffer.readInt();
			this.x = buffer.readInt();
			this.y = buffer.readInt();
			this.z = buffer.readInt();
			this.changeType = buffer.readInt();
			this.meta = buffer.readInt();
		}

		public static void buffer(GUISlotChangedMessage message, PacketBuffer buffer) {
			buffer.writeInt(message.slotID);
			buffer.writeInt(message.x);
			buffer.writeInt(message.y);
			buffer.writeInt(message.z);
			buffer.writeInt(message.changeType);
			buffer.writeInt(message.meta);
		}

		public static void handler(GUISlotChangedMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				PlayerEntity entity = context.getSender();
				int slotID = message.slotID;
				int changeType = message.changeType;
				int meta = message.meta;
				int x = message.x;
				int y = message.y;
				int z = message.z;

				handleSlotAction(entity, slotID, changeType, meta, x, y, z);
			});
			context.setPacketHandled(true);
		}

	}

	private static void handleButtonAction(PlayerEntity entity, int buttonID, int x, int y, int z) {
		World world = entity.world;

		// security measure to prevent arbitrary chunk generation
		if (!world.isBlockLoaded(new BlockPos(x, y, z)))
			return;

		<#assign btid = 0>
        <#list data.components as component>
			<#if component.getClass().getSimpleName() == "Button">
				<#if hasProcedure(component.onClick)>
        	    	if (buttonID == ${btid}) {
        	    	    <@procedureOBJToCode component.onClick/>
					}
				</#if>
				<#assign btid +=1>
			</#if>
		</#list>
	}

	private static void handleSlotAction(PlayerEntity entity, int slotID, int changeType, int meta, int x, int y, int z) {
		World world = entity.world;

		// security measure to prevent arbitrary chunk generation
		if (!world.isBlockLoaded(new BlockPos(x, y, z)))
			return;

		<#list data.components as component>
			<#if component.getClass().getSimpleName()?ends_with("Slot")>
				<#if hasProcedure(component.onSlotChanged)>
					if (slotID == ${component.id} && changeType == 0) {
						<@procedureOBJToCode component.onSlotChanged/>
					}
				</#if>
				<#if hasProcedure(component.onTakenFromSlot)>
					if (slotID == ${component.id} && changeType == 1) {
						<@procedureOBJToCode component.onTakenFromSlot/>
					}
				</#if>
				<#if hasProcedure(component.onStackTransfer)>
					if (slotID == ${component.id} && changeType == 2) {
						int amount = meta;
						<@procedureOBJToCode component.onStackTransfer/>
					}
				</#if>
			</#if>
		</#list>
	}

}
<#-- @formatter:on -->