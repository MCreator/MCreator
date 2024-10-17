<#-- @formatter:off -->
<#include "mcitems.ftl">
<#include "procedures.java.ftl">
<#include "tokens.ftl">

package ${package}.gui;

@Elements${JavaModName}.ModElement.Tag public class Gui${name} extends Elements${JavaModName}.ModElement{

	public static int GUIID = ${data.getModElement().getID(0)};
	public static HashMap guistate = new HashMap();

    <#assign w = (data.width/2)?round>
    <#assign h = (data.height/2)?round>

    <#assign mx = (data.W/2 - w)?round>
    <#assign my = (data.H/2 - h)?round>

	<#assign slotnum = 0>

	public Gui${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void preInit(FMLPreInitializationEvent event) {
		elements.addNetworkMessage(GUIButtonPressedMessageHandler.class, GUIButtonPressedMessage.class, Side.SERVER);
		elements.addNetworkMessage(GUISlotChangedMessageHandler.class, GUISlotChangedMessage.class, Side.SERVER);
	}

	public static class GuiContainerMod extends Container implements Supplier<Map<Integer, Slot>> {

		private IInventory internal;

		private World world;
		private EntityPlayer entity;
		private int x, y, z;

		private Map<Integer, Slot> customSlots = new HashMap<>();

		public GuiContainerMod(World world, int x, int y, int z, EntityPlayer player) {
			this.world = world;
			this.entity = player;
			this.x = x;
			this.y = y;
			this.z = z;

			this.internal = new InventoryBasic("", true, ${data.getMaxSlotID() + 1});

			<#if data.type == 1>

			    TileEntity ent = world.getTileEntity(new BlockPos(x, y, z));
			    if (ent instanceof IInventory)
					this.internal = (IInventory) ent;

                <#list data.components as component>
                    <#if component.getClass().getSimpleName()?ends_with("Slot")>
					<#assign slotnum += 1>
                    this.customSlots.put(${component.id}, this.addSlotToContainer(
							new Slot(internal, ${component.id}, ${(component.x/2 - mx/2 + 1)?round}, ${(component.y/2 - my/2 + 1)?round}) {

						<#if hasProcedure(component.onSlotChanged)>
                        @Override public void onSlotChanged() {
							super.onSlotChanged();
							GuiContainerMod.this.slotChanged(${component.id}, 0, 0);
						}
						</#if>

						<#if hasProcedure(component.onTakenFromSlot)>
                        @Override public ItemStack onTake(EntityPlayer entity, ItemStack stack) {
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

                        <#if component.getClass().getSimpleName() == "InputSlot">
                            <#if component.inputLimit.toString()?has_content>
                             @Override public boolean isItemValid(ItemStack stack) {
								 <#if hasMetadata(component.inputLimit)>
			                     return (${mappedMCItemToItemStackCode(component.inputLimit,1)}.getItem() == stack
										 .getItem() && ${mappedMCItemToItemStackCode(component.inputLimit,1)}.
									 getMetadata() == stack.getMetadata());
                                 <#else>
                                 return (${mappedMCItemToItemStackCode(component.inputLimit,1)}.getItem() == stack
										 .getItem());
                                 </#if>
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

                <#assign coffx = ((w - 176)?abs / 2)?round>
                <#assign coffy = ((h - 166)?abs / 2)?round>

                int si;
			    int sj;

			    for (si = 0; si < 3; ++si)
					for (sj = 0; sj < 9; ++sj)
						this.addSlotToContainer(
								new Slot(player.inventory, sj + (si + 1) * 9, ${coffx} + 8 + sj * 18,${coffy}
										+ 84 + si * 18));

			    for (si = 0; si < 9; ++si)
					this.addSlotToContainer(new Slot(player.inventory, si, ${coffx} + 8 + si * 18, ${coffy} + 142));

            </#if>
		}

		public Map<Integer, Slot> get() {
			return customSlots;
		}

		@Override public boolean canInteractWith(EntityPlayer player) {
			return internal.isUsableByPlayer(player);
		}

		<#if data.type == 1>
		@Override public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
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

		@Override ${mcc.getMethod("net.minecraft.inventory.Container", "mergeItemStack", "ItemStack", "int", "int", "boolean")
					   .replace("slot.onSlotChanged();", "slot.putStack(itemstack);")
					   .replace("!itemstack.isEmpty()", "slot.isItemValid(itemstack) && !itemstack.isEmpty()")}
        </#if>

		@Override public void onContainerClosed(EntityPlayer playerIn) {
			super.onContainerClosed(playerIn);
			if ((internal instanceof InventoryBasic) && (playerIn instanceof EntityPlayerMP)) {
				this.clearContainer(playerIn, playerIn.world, internal);
			}
		}

		private void slotChanged(int slotid, int ctype, int meta) {
			if(this.world != null && this.world.isRemote) {
				${JavaModName}.PACKET_HANDLER.sendToServer(new GUISlotChangedMessage(slotid, x, y, z, ctype, meta));
				handleSlotAction(entity, slotid, ctype, meta, x, y, z);
			}
		}

	}

	public static class GuiWindow extends GuiContainer {

		private World world;
		private int x, y, z;
		private EntityPlayer entity;

		<#list data.components as component>
            <#if component.getClass().getSimpleName() == "TextField">
            GuiTextField ${component.name};
            </#if>
        </#list>

		public GuiWindow(World world, int x, int y, int z, EntityPlayer entity) {
			super(new GuiContainerMod(world, x, y, z, entity));
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
			this.entity = entity;
			this.xSize = ${w}; this.ySize = ${h};
		}

		<#if data.renderBgLayer>
		private static final ResourceLocation texture = new ResourceLocation("${modid}:textures/${registryname}.png" );
		</#if>

		@Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			this.drawDefaultBackground();
			super.drawScreen(mouseX, mouseY, partialTicks);
			this.renderHoveredToolTip(mouseX, mouseY);
		}

		@Override protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			<#if data.renderBgLayer>
			this.mc.renderEngine.bindTexture(texture);
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;
			this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
			</#if>

			zLevel = 100.0F;

			<#list data.components as component>
                <#if component.getClass().getSimpleName() == "Image">
					this.mc.renderEngine.bindTexture(new ResourceLocation("${modid}:textures/${component.image}"));
                this.drawTexturedModalRect(this.guiLeft + ${(component.x/2 - mx/2)?round},
						this.guiTop + ${(component.y/2 - my/2)?round}, 0, 0, 256, 256);
                </#if>
            </#list>
		}

		@Override public void updateScreen() {
			super.updateScreen();
			<#list data.components as component>
                <#if component.getClass().getSimpleName() == "TextField">
                    ${component.name}.updateCursorCounter();
                </#if>
            </#list>
		}

		@Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
			<#list data.components as component>
        	    <#if component.getClass().getSimpleName() == "TextField">
        	    ${component.name}.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
        	    </#if>
        	</#list>

			super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		@Override protected void keyTyped(char typedChar, int keyCode) throws IOException {
			<#list data.components as component>
        	    <#if component.getClass().getSimpleName() == "TextField">
        	    ${component.name}.textboxKeyTyped(typedChar, keyCode);
        	    if(${component.name}.isFocused())
        	    	return;
        	    </#if>
        	</#list>
			super.keyTyped(typedChar, keyCode);
		}

		@Override protected void drawGuiContainerForegroundLayer(int par1, int par2) {
            <#list data.components as component>
                <#if component.getClass().getSimpleName() == "TextField">
                ${component.name}.drawTextBox();
                <#elseif component.getClass().getSimpleName() == "Label">
                this.fontRenderer.drawString("${translateTokens(JavaConventions.escapeStringForJava(component.text))}",
                    ${(component.x/2 - mx/2)?round}, ${(component.y/2 - my/2)?round}, ${component.color.getRGB()});
                </#if>
            </#list>
		}

		@Override public void onGuiClosed() {
			super.onGuiClosed();
			Keyboard.enableRepeatEvents(false);
		}

		@Override public void initGui() {
			super.initGui();

			this.guiLeft = (this.width - ${w}) / 2; this.guiTop = (this.height - ${h}) / 2;

			Keyboard.enableRepeatEvents(true);

			this.buttonList.clear();

			<#assign btid = 0>
            <#assign tfid = 0>
			<#list data.components as component>
                <#if component.getClass().getSimpleName() == "TextField">
                    ${component.name} =
					new GuiTextField(${tfid},
							this.fontRenderer, ${(component.x/2 - mx/2)?round}, ${(component.y/2 - my/2)?round},
                        ${(component.width/2)?round}, ${(component.height/2)?round});
                    guistate.put("text:${component.name}", ${component.name});
                    ${component.name}.setMaxStringLength(32767);
                    ${component.name}.setText("${component.placeholder}");
                    <#assign tfid +=1>
                <#elseif component.getClass().getSimpleName() == "Button">
                    this.buttonList.add(new GuiButton(${btid}, this.guiLeft + ${(component.x/2 - mx/2)?round},
							this.guiTop
									+ ${(component.y/2 - my/2)?round}, ${(component.width/2)?round}, ${(component.height/2)?round},
							"${component.text}"));
                    <#assign btid +=1>
                </#if>
            </#list>
		}

		@Override protected void actionPerformed(GuiButton button) {
			${JavaModName}.PACKET_HANDLER.sendToServer(new GUIButtonPressedMessage(button.id, x, y, z));
			handleButtonAction(entity, button.id, x, y, z);
		}

		@Override public boolean doesGuiPauseGame() {
			return false;
		}

	}

	public static class GUIButtonPressedMessageHandler implements IMessageHandler<GUIButtonPressedMessage, IMessage> {

		@Override public IMessage onMessage(GUIButtonPressedMessage message, MessageContext context) {
	    	EntityPlayerMP entity = context.getServerHandler().player;
	    	entity.getServerWorld().addScheduledTask(() -> {
	    		int buttonID = message.buttonID;
	    		int x = message.x;
	    		int y = message.y;
	    		int z = message.z;

	    		handleButtonAction(entity, buttonID, x, y, z);
	    	});
	    	return null;
		}
	}

	public static class GUISlotChangedMessageHandler implements IMessageHandler<GUISlotChangedMessage, IMessage> {

		@Override public IMessage onMessage(GUISlotChangedMessage message, MessageContext context) {
	    	EntityPlayerMP entity = context.getServerHandler().player;
	    	entity.getServerWorld().addScheduledTask(() -> {
	    		int slotID = message.slotID;
	    		int changeType = message.changeType;
	    		int meta = message.meta;
	    		int x = message.x;
	    		int y = message.y;
	    		int z = message.z;

				handleSlotAction(entity, slotID, changeType, meta, x, y, z);
	    	});
	    	return null;
		}
	}

	public static class GUIButtonPressedMessage implements IMessage {

  		int buttonID, x, y, z;

  		public GUIButtonPressedMessage() {
		}

  		public GUIButtonPressedMessage(int buttonID, int x, int y, int z) {
  		  this.buttonID = buttonID;
  		  this.x = x;
  		  this.y = y;
  		  this.z = z;
  		}

		@Override public void toBytes(io.netty.buffer.ByteBuf buf) {
  			buf.writeInt(buttonID);
  			buf.writeInt(x);
  			buf.writeInt(y);
  			buf.writeInt(z);
		}

		@Override public void fromBytes(io.netty.buffer.ByteBuf buf) {
  			buttonID = buf.readInt();
  			x = buf.readInt();
  			y = buf.readInt();
  			z = buf.readInt();
		}

	}

	public static class GUISlotChangedMessage implements IMessage {

  		int slotID, x, y, z, changeType, meta;

  		public GUISlotChangedMessage() {
		}

  		public GUISlotChangedMessage(int slotID, int x, int y, int z, int changeType, int meta) {
  		  this.slotID = slotID;
  		  this.x = x;
  		  this.y = y;
  		  this.z = z;
  		  this.changeType = changeType;
  		  this.meta = meta;
  		}

		@Override public void toBytes(io.netty.buffer.ByteBuf buf) {
  			buf.writeInt(slotID);
  			buf.writeInt(x);
  			buf.writeInt(y);
  			buf.writeInt(z);
  			buf.writeInt(changeType);
  			buf.writeInt(meta);
		}

		@Override public void fromBytes(io.netty.buffer.ByteBuf buf) {
  			slotID = buf.readInt();
  			x = buf.readInt();
  			y = buf.readInt();
  			z = buf.readInt();
  			changeType = buf.readInt();
  			meta = buf.readInt();
		}

	}

	private static void handleButtonAction(EntityPlayer entity, int buttonID, int x, int y, int z) {
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

	private static void handleSlotAction(EntityPlayer entity, int slotID, int changeType, int meta, int x, int y, int z) {
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