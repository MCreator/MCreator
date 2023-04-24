<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2022, Pylo, opensource contributors
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
<#include "../procedures.java.ftl">
package ${package}.client.gui;

<#assign mx = data.W - data.width>
<#assign my = data.H - data.height>

public class ${name}Screen extends AbstractContainerScreen<${name}Menu> {

	private final static HashMap<String, Object> guistate = ${name}Menu.guistate;

	private final Level world;
	private final int x, y, z;
	private final Player entity;

	<#list data.getComponentsOfType("TextField") as component>
		EditBox ${component.getName()};
	</#list>

	<#list data.getComponentsOfType("Checkbox") as component>
		Checkbox ${component.getName()};
	</#list>

	<#list data.getComponentsOfType("Button") as component>
		Button ${component.getName()};
	</#list>

	<#list data.getComponentsOfType("ImageButton") as component>
		ImageButton ${component.getName()};
	</#list>

	public ${name}Screen(${name}Menu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = ${data.width};
		this.imageHeight = ${data.height};
	}

	<#if data.doesPauseGame>
		@Override public boolean isPauseScreen() {
			return true;
		}
	</#if>

	<#if data.renderBgLayer>
		private static final ResourceLocation texture = new ResourceLocation("${modid}:textures/screens/${registryname}.png" );
	</#if>

	@Override public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(ms);
		super.render(ms, mouseX, mouseY, partialTicks);
		this.renderTooltip(ms, mouseX, mouseY);

		<#list data.getComponentsOfType("TextField") as component>
				${component.getName()}.render(ms, mouseX, mouseY, partialTicks);
		</#list>

		<#list data.getComponentsOfType("EntityModel") as component>
			<#assign followMouse = component.followMouseMovement>
			<#assign x = (component.x - mx/2)?int>
			<#assign y = (component.y - my/2)?int>
			if (<@procedureOBJToConditionCode component.entityModel/> instanceof LivingEntity livingEntity) {
				<#if hasProcedure(component.displayCondition)>
					if (<@procedureOBJToConditionCode component.displayCondition/>)
				</#if>
				InventoryScreen.renderEntityInInventoryRaw(this.leftPos + ${x + 11}, this.topPos + ${y + 21}, ${component.scale},
					${component.rotationX / 20.0}f <#if followMouse> + (float) Math.atan((this.leftPos + ${x + 11} - mouseX) / 40.0)</#if>,
					<#if followMouse>(float) Math.atan((this.topPos + ${y + 21 - 50} - mouseY) / 40.0)<#else>0</#if>,
					livingEntity
				);
			}
		</#list>
	}

	@Override protected void renderBg(PoseStack ms, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		<#if data.renderBgLayer>
			RenderSystem.setShaderTexture(0, texture);
			this.blit(ms, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		</#if>

		<#list data.getComponentsOfType("Image") as component>
			<#if hasProcedure(component.displayCondition)>if (<@procedureOBJToConditionCode component.displayCondition/>) {</#if>
				RenderSystem.setShaderTexture(0, new ResourceLocation("${modid}:textures/screens/${component.image}"));
				this.blit(ms, this.leftPos + ${(component.x - mx/2)?int}, this.topPos + ${(component.y - my/2)?int}, 0, 0,
					${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())},
					${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())});
			<#if hasProcedure(component.displayCondition)>}</#if>
		</#list>

		RenderSystem.disableBlend();
	}

	@Override public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}

		<#list data.getComponentsOfType("TextField") as component>
				if(${component.getName()}.isFocused())
					return ${component.getName()}.keyPressed(key, b, c);
		</#list>

		return super.keyPressed(key, b, c);
	}

	@Override public void containerTick() {
		super.containerTick();
		<#list data.getComponentsOfType("TextField") as component>
				${component.getName()}.tick();
		</#list>
	}

	@Override protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		<#list data.getComponentsOfType("Label") as component>
			<#if hasProcedure(component.displayCondition)>
				if (<@procedureOBJToConditionCode component.displayCondition/>)
			</#if>
			this.font.draw(poseStack,
				<#if hasProcedure(component.text)><@procedureOBJToStringCode component.text/><#else>Component.translatable("gui.${modid}.${registryname}.${component.getName()}")</#if>,
				${(component.x - mx / 2)?int}, ${(component.y - my / 2)?int}, ${component.color.getRGB()});
		</#list>
	}

	@Override public void onClose() {
		super.onClose();
		Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override public void init() {
		super.init();

		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

		<#list data.getComponentsOfType("TextField") as component>
			${component.getName()} = new EditBox(this.font, this.leftPos + ${(component.x - mx/2)?int}, this.topPos + ${(component.y - my/2)?int},
			${component.width}, ${component.height}, Component.translatable("gui.${modid}.${registryname}.${component.getName()}"))
			<#if component.placeholder?has_content>
			{
				{
					setSuggestion(Component.translatable("gui.${modid}.${registryname}.${component.getName()}").getString());
				}

				@Override public void insertText(String text) {
					super.insertText(text);

					if (getValue().isEmpty())
						setSuggestion(Component.translatable("gui.${modid}.${registryname}.${component.getName()}").getString());
					else
						setSuggestion(null);
				}

				@Override public void moveCursorTo(int pos) {
					super.moveCursorTo(pos);

					if (getValue().isEmpty())
						setSuggestion(Component.translatable("gui.${modid}.${registryname}.${component.getName()}").getString());
					else
						setSuggestion(null);
				}
			}
			</#if>;
			${component.getName()}.setMaxLength(32767);

			guistate.put("text:${component.getName()}", ${component.getName()});
			this.addWidget(this.${component.getName()});
		</#list>

		<#assign btid = 0>

		<#list data.getComponentsOfType("Button") as component>
			${component.getName()} = new Button(
				this.leftPos + ${(component.x - mx/2)?int}, this.topPos + ${(component.y - my/2)?int},
				${component.width}, ${component.height},
				Component.translatable("gui.${modid}.${registryname}.${component.getName()}"),
				<@buttonOnClick component/>
			)<@buttonDisplayCondition component/>;

			guistate.put("button:${component.getName()}", ${component.getName()});
			this.addRenderableWidget(${component.getName()});

			<#assign btid +=1>
		</#list>

		<#list data.getComponentsOfType("ImageButton") as component>
		    ${component.getName()} = new ImageButton(
				this.leftPos + ${(component.x - mx/2)?int}, this.topPos + ${(component.y - my/2)?int},
            	${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())},
				0, 0, ${component.getHeight(w.getWorkspace())},
            	new ResourceLocation("${modid}:textures/screens/atlas/${component.getName()}.png"),
            	${component.getWidth(w.getWorkspace())},
				${component.getHeight(w.getWorkspace()) * 2},
				<@buttonOnClick component/>
			)<@buttonDisplayCondition component/>;

			guistate.put("button:${component.getName()}", ${component.getName()});
			this.addRenderableWidget(${component.getName()});

			<#assign btid +=1>
		</#list>

		<#list data.getComponentsOfType("Checkbox") as component>
			${component.getName()} = new Checkbox(this.leftPos + ${(component.x - mx/2)?int}, this.topPos + ${(component.y - my/2)?int},
					20, 20, Component.translatable("gui.${modid}.${registryname}.${component.getName()}"), <#if hasProcedure(component.isCheckedProcedure)>
				<@procedureOBJToConditionCode component.isCheckedProcedure/><#else>false</#if>);

			guistate.put("checkbox:${component.getName()}", ${component.getName()});
			this.addRenderableWidget(${component.getName()});
		</#list>
	}

}

<#macro buttonOnClick component>
e -> {
    <#if hasProcedure(component.onClick)>
	    if (<@procedureOBJToConditionCode component.displayCondition/>) {
			${JavaModName}.PACKET_HANDLER.sendToServer(new ${name}ButtonMessage(${btid}, x, y, z));
			${name}ButtonMessage.handleButtonAction(entity, ${btid}, x, y, z);
		}
	</#if>
}
</#macro>

<#macro buttonDisplayCondition component>
<#if hasProcedure(component.displayCondition)>
{
	@Override public void render(PoseStack ms, int gx, int gy, float ticks) {
		if (<@procedureOBJToConditionCode component.displayCondition/>)
			super.render(ms, gx, gy, ticks);
	}
}
</#if>
</#macro>
<#-- @formatter:on -->