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
<#include "../procedures.java.ftl">
<#include "../tokens.ftl">

package ${package}.client.gui;

public class ${name}Screen extends AbstractContainerScreen<${name}Menu> {

	public final static HashMap<String, Object> guistate = new HashMap<>();

	private final Level world;
	private final int x, y, z;
	private final Player entity;

	<#list data.components as component>
		<#if component.getClass().getSimpleName() == "TextField">
	    EditBox ${component.name};
		<#elseif component.getClass().getSimpleName() == "Checkbox">
	    Checkbox ${component.name};
		</#if>
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
	private static final ResourceLocation texture = new ResourceLocation("${modid}:textures/${registryname}.png" );
	</#if>

	@Override public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(ms);
		super.render(ms, mouseX, mouseY, partialTicks);
		this.renderTooltip(ms, mouseX, mouseY);

		<#list data.components as component>
			<#if component.getClass().getSimpleName() == "TextField">
				${component.name}.render(ms, mouseX, mouseY, partialTicks);
			</#if>
		</#list>
	}

	@Override protected void renderBg(PoseStack ms, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		<#if data.renderBgLayer>
		RenderSystem.setShaderTexture(0, texture);
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;
		this.blit(ms, k, l, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		</#if>

		<#list data.components as component>
			<#if component.getClass().getSimpleName() == "Image">
				<#if hasProcedure(component.displayCondition)>if (<@procedureOBJToConditionCode component.displayCondition/>) {</#if>
					RenderSystem.setShaderTexture(0, new ResourceLocation("${modid}:textures/${component.image}"));
					this.blit(ms, ${component.x}, ${component.y}, 0, 0,
						${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())},
						${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())});
				<#if hasProcedure(component.displayCondition)>}</#if>
			</#if>
		</#list>

		RenderSystem.disableBlend();
	}

	@Override public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
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

	@Override public void containerTick() {
		super.containerTick();
		<#list data.components as component>
			<#if component.getClass().getSimpleName() == "TextField">
				${component.name}.tick();
			</#if>
		</#list>
	}

	@Override protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		<#list data.components as component>
			<#if component.getClass().getSimpleName() == "Label">
				<#if hasProcedure(component.displayCondition)>
				if (<@procedureOBJToConditionCode component.displayCondition/>)
				</#if>
		    	drawString(poseStack, this.font, "${translateTokens(JavaConventions.escapeStringForJava(component.text))}",
					${component.x} - this.leftPos, ${component.y} - this.topPos, ${component.color.getRGB()});
			</#if>
		</#list>
	}

	@Override public void onClose() {
		super.onClose();
		Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override public void init() {
		super.init();

		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

		<#assign btid = 0>
		<#list data.components as component>
			<#if component.getClass().getSimpleName() == "TextField">
				${component.name} = new EditBox(this.font, ${component.x}, ${component.y},
				${component.width}, ${component.height}, new TextComponent("${component.placeholder}"))
				<#if component.placeholder?has_content>
				{
					{
						setSuggestion("${component.placeholder}");
					}

					@Override public void insertText(String text) {
						super.insertText(text);

						if(getValue().isEmpty())
							setSuggestion("${component.placeholder}");
						else
							setSuggestion(null);
					}

					@Override public void moveCursorTo(int pos) {
						super.moveCursorTo(pos);

						if(getValue().isEmpty())
							setSuggestion("${component.placeholder}");
						else
							setSuggestion(null);
					}
				}
				</#if>;
                guistate.put("text:${component.name}", ${component.name});
				${component.name}.setMaxLength(32767);
				this.addWidget(this.${component.name});
			<#elseif component.getClass().getSimpleName() == "Button">
				this.addRenderableWidget(new Button(${component.x}, ${component.y}, ${component.width}, ${component.height},
						new TextComponent("${component.text}"), e -> {
							if (<@procedureOBJToConditionCode component.displayCondition/>) {
								${JavaModName}.PACKET_HANDLER.sendToServer(new ${name}ButtonMessage(${btid}, x, y, z));
								${name}ButtonMessage.handleButtonAction(entity, ${btid}, x, y, z);
							}
					}
				)
                <#if hasProcedure(component.displayCondition)>
                {
					@Override public void render(PoseStack ms, int gx, int gy, float ticks) {
						if (<@procedureOBJToConditionCode component.displayCondition/>)
							super.render(ms, gx, gy, ticks);
					}
				}
				</#if>);
				<#assign btid +=1>
			<#elseif component.getClass().getSimpleName() == "Checkbox">
            	${component.name} = new Checkbox(${component.x}, ${component.y}, 150, 20,
						new TextComponent("${component.text}"), <#if hasProcedure(component.isCheckedProcedure)>
            	    <@procedureOBJToConditionCode component.isCheckedProcedure/><#else>false</#if>);
                guistate.put("checkbox:${component.name}", ${component.name});
                this.addRenderableWidget(${component.name});
			</#if>
		</#list>
	}

}
<#-- @formatter:on -->