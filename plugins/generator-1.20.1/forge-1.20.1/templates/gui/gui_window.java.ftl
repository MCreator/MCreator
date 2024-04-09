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
<#include "../procedures.java.ftl">
package ${package}.client.gui;

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

	@Override public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);

		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		<#list data.getComponentsOfType("TextField") as component>
				${component.getName()}.render(guiGraphics, mouseX, mouseY, partialTicks);
		</#list>

		<#list data.getComponentsOfType("EntityModel") as component>
			<#assign followMouse = component.followMouseMovement>
			<#assign x = component.gx(data.width)>
			<#assign y = component.gy(data.height)>
			if (<@procedureOBJToConditionCode component.entityModel/> instanceof LivingEntity livingEntity) {
				<#if hasProcedure(component.displayCondition)>
					if (<@procedureOBJToConditionCode component.displayCondition/>)
				</#if>
				InventoryScreen.renderEntityInInventoryFollowsAngle(guiGraphics, this.leftPos + ${x + 10}, this.topPos + ${y + 20}, ${component.scale},
					${component.rotationX / 20.0}f <#if followMouse> + (float) Math.atan((this.leftPos + ${x + 10} - mouseX) / 40.0)</#if>,
					<#if followMouse>(float) Math.atan((this.topPos + ${y + 21 - 50} - mouseY) / 40.0)<#else>0</#if>,
					livingEntity
				);
			}
		</#list>

		this.renderTooltip(guiGraphics, mouseX, mouseY);

		<#list data.getComponentsOfType("Tooltip") as component>
			<#assign x = component.gx(data.width)>
			<#assign y = component.gy(data.height)>
			<#if hasProcedure(component.displayCondition)>
				if (<@procedureOBJToConditionCode component.displayCondition/>)
			</#if>
				if (mouseX > leftPos + ${x} && mouseX < leftPos + ${x + component.width} && mouseY > topPos + ${y} && mouseY < topPos + ${y + component.height})
					guiGraphics.renderTooltip(font, <#if hasProcedure(component.text)>Component.literal(<@procedureOBJToStringCode component.text/>)<#else>Component.translatable("gui.${modid}.${registryname}.${component.getName()}")</#if>, mouseX, mouseY);
		</#list>
	}

	@Override protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		<#if data.renderBgLayer>
			guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		</#if>

		<#list data.getComponentsOfType("Image") as component>
			<#if hasProcedure(component.displayCondition)>if (<@procedureOBJToConditionCode component.displayCondition/>) {</#if>
				guiGraphics.blit(new ResourceLocation("${modid}:textures/screens/${component.image}"),
					this.leftPos + ${component.gx(data.width)}, this.topPos + ${component.gy(data.height)}, 0, 0,
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

	@Override protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		<#list data.getComponentsOfType("Label") as component>
			<#if hasProcedure(component.displayCondition)>
				if (<@procedureOBJToConditionCode component.displayCondition/>)
			</#if>
			guiGraphics.drawString(this.font,
				<#if hasProcedure(component.text)><@procedureOBJToStringCode component.text/><#else>Component.translatable("gui.${modid}.${registryname}.${component.getName()}")</#if>,
				${component.gx(data.width)}, ${component.gy(data.height)}, ${component.color.getRGB()}, false);
		</#list>
	}

	@Override public void init() {
		super.init();

		<#list data.getComponentsOfType("TextField") as component>
			${component.getName()} = new EditBox(this.font, this.leftPos + ${component.gx(data.width) + 1}, this.topPos + ${component.gy(data.height) + 1},
			${component.width - 2}, ${component.height - 2}, Component.translatable("gui.${modid}.${registryname}.${component.getName()}"))
			<#if component.placeholder?has_content>
			{
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
			<#if component.placeholder?has_content>
			${component.getName()}.setSuggestion(Component.translatable("gui.${modid}.${registryname}.${component.getName()}").getString());
			</#if>
			${component.getName()}.setMaxLength(32767);

			guistate.put("text:${component.getName()}", ${component.getName()});
			this.addWidget(this.${component.getName()});
		</#list>

		<#assign btid = 0>

		<#list data.getComponentsOfType("Button") as component>
			<#if component.isUndecorated>
				${component.getName()} = new PlainTextButton(
					this.leftPos + ${component.gx(data.width)}, this.topPos + ${component.gy(data.height)},
					${component.width}, ${component.height},
					Component.translatable("gui.${modid}.${registryname}.${component.getName()}"),
					<@buttonOnClick component/>, this.font
				)<@buttonDisplayCondition component/>;
			<#else>
				${component.getName()} = Button.builder(Component.translatable("gui.${modid}.${registryname}.${component.getName()}"), <@buttonOnClick component/>)
					.bounds(this.leftPos + ${component.gx(data.width)}, this.topPos + ${component.gy(data.height)},
					${component.width}, ${component.height})
					<#if hasProcedure(component.displayCondition)>
						.build(builder -> new Button(builder)<@buttonDisplayCondition component/>);
					<#else>
						.build();
					</#if>
			</#if>

			guistate.put("button:${component.getName()}", ${component.getName()});
			this.addRenderableWidget(${component.getName()});

			<#assign btid +=1>
		</#list>

		<#list data.getComponentsOfType("ImageButton") as component>
			${component.getName()} = new ImageButton(
				this.leftPos + ${component.gx(data.width)}, this.topPos + ${component.gy(data.height)},
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
			${component.getName()} = new Checkbox(this.leftPos + ${component.gx(data.width)}, this.topPos + ${component.gy(data.height)},
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
	@Override public void render(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
		if (<@procedureOBJToConditionCode component.displayCondition/>)
			super.render(guiGraphics, gx, gy, ticks);
	}
}
</#if>
</#macro>
<#-- @formatter:on -->
