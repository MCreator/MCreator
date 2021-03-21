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
<#include "tokens.ftl">

<#assign mx = data.W - data.width>
<#assign my = data.H - data.height>

package ${package}.gui;

import ${package}.${JavaModName};

@OnlyIn(Dist.CLIENT) public class ${name}GuiWindow extends ContainerScreen<${name}Gui.GuiContainerMod> {

	private World world;
	private int x, y, z;
	private PlayerEntity entity;

	<#list data.components as component>
		<#if component.getClass().getSimpleName() == "TextField">
	    TextFieldWidget ${component.name};
		</#if>
	</#list>

	public ${name}GuiWindow(${name}Gui.GuiContainerMod container, PlayerInventory inventory, ITextComponent text) {
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
	    private static final ResourceLocation texture = new ResourceLocation("${modid}:textures/<#if data.customBg?has_content>${data.customBg}<#else>${registryname}.png</#if>");
	</#if>

	@Override public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(ms);
		super.render(ms, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(ms, mouseX, mouseY);

		<#list data.components as component>
			<#if component.getClass().getSimpleName() == "TextField">
				${component.name}.render(ms, mouseX, mouseY, partialTicks);
			</#if>
		</#list>
	}

	@Override protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float par1, int par2, int par3) {
		GL11.glColor4f(1, 1, 1, 1);

		<#if data.renderBgLayer>
		Minecraft.getInstance().getTextureManager().bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.blit(ms, k, l, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize);
		</#if>

		<#list data.components as component>
			<#if component.getClass().getSimpleName() == "Image">
				<#if hasCondition(component.displayCondition)>
				if (<@procedureOBJToConditionCode component.displayCondition/>) {
				</#if>
					Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("${modid}:textures/${component.image}"));
					this.blit(ms, this.guiLeft + ${(component.x - mx/2)?int}, this.guiTop + ${(component.y - my/2)?int}, 0, 0,
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

	@Override protected void drawGuiContainerForegroundLayer(MatrixStack ms, int mouseX, int mouseY) {
		<#list data.components as component>
			<#if component.getClass().getSimpleName() == "Label">
				<#if hasCondition(component.displayCondition)>
				if (<@procedureOBJToConditionCode component.displayCondition/>)
				</#if>
		    	this.font.drawString(ms, "${translateTokens(JavaConventions.escapeStringForJava(component.text))}",
					${(component.x - mx / 2)?int}, ${(component.y - my / 2)?int}, ${component.color.getRGB()});
			</#if>
		</#list>
	}

	@Override public void onClose() {
		super.onClose();
		Minecraft.getInstance().keyboardListener.enableRepeatEvents(false);
	}

	@Override public void init(Minecraft minecraft, int width, int height) {
		super.init(minecraft, width, height);
		minecraft.keyboardListener.enableRepeatEvents(true);

		<#assign btid = 0>
		<#list data.components as component>
			<#if component.getClass().getSimpleName() == "TextField">
				${component.name} = new TextFieldWidget(this.font, this.guiLeft + ${(component.x - mx/2)?int}, this.guiTop + ${(component.y - my/2)?int},
				${component.width}, ${component.height}, new StringTextComponent("${component.placeholder}"))
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
                ${name}Gui.guistate.put("text:${component.name}", ${component.name});
				${component.name}.setMaxStringLength(32767);
                this.children.add(this.${component.name});
			<#elseif component.getClass().getSimpleName() == "Button">
				this.addButton(new Button(this.guiLeft + ${(component.x - mx/2)?int}, this.guiTop + ${(component.y - my/2)?int},
					${component.width}, ${component.height}, new StringTextComponent("${component.text}"), e -> {
					${JavaModName}.PACKET_HANDLER.sendToServer(new ${name}Gui.ButtonPressedMessage(${btid}, x, y, z));

					${name}Gui.handleButtonAction(entity, ${btid}, x, y, z);
				})
                <#if hasCondition(component.displayCondition)>
                {
					@Override public void render(MatrixStack ms, int x, int y, float ticks) {
						if (<@procedureOBJToConditionCode component.displayCondition/>)
							super.render(ms, x, y, ticks);
					}
				}
				</#if>);
				<#assign btid +=1>
			</#if>
		</#list>
	}

}
<#-- @formatter:on -->