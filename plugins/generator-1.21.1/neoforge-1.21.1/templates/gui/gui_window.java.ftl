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

<#assign textFields = data.getComponentsOfType("TextField")>
<#assign checkboxes = data.getComponentsOfType("Checkbox")>
<#assign buttons = data.getComponentsOfType("Button")>
<#assign imageButtons = data.getComponentsOfType("ImageButton")>
<#assign sliders = data.getComponentsOfType("Slider")>

<#compress>
public class ${name}Screen extends AbstractContainerScreen<${name}Menu> implements ${JavaModName}Screens.ScreenAccessor {

	private final Level world;
	private final int x, y, z;
	private final Player entity;

	private boolean menuStateUpdateActive = false;

	<#list textFields as component>
	EditBox ${component.getName()};
	</#list>

	<#list checkboxes as component>
	Checkbox ${component.getName()};
	</#list>

	<#list buttons as component>
	Button ${component.getName()};
	</#list>

	<#list imageButtons as component>
	ImageButton ${component.getName()};
	</#list>

	<#list sliders as component>
	ExtendedSlider ${component.getName()};
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

	@Override public void updateMenuState(int elementType, String name, Object elementState) {
		menuStateUpdateActive = true;

		<#if textFields?has_content>
		if (elementType == 0 && elementState instanceof String stringState) {
			<#list textFields as component>
				<#if !component?is_first>else</#if> if (name.equals("${component.getName()}"))
					${component.getName()}.setValue(stringState);
			</#list>
		}
		</#if>

		<#-- updateMenuState is not implemented for checkboxes, as there is no procedure block to set checkbox state currently -->

		menuStateUpdateActive = false;
	}

	<#if data.doesPauseGame>
	@Override public boolean isPauseScreen() {
		return true;
	}
	</#if>

	<#if data.renderBgLayer>
	private static final ResourceLocation texture = ResourceLocation.parse("${modid}:textures/screens/${registryname}.png");
	</#if>

	@Override public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		<#list textFields as component>
		${component.getName()}.render(guiGraphics, mouseX, mouseY, partialTicks);
		</#list>

		<#compress>
		<#list data.getComponentsOfType("EntityModel") as component>
			<#assign followMouse = component.followMouseMovement>
			<#assign x = component.gx(data.width)>
			<#assign y = component.gy(data.height)>
			if (<@procedureOBJToConditionCode component.entityModel/> instanceof LivingEntity livingEntity) {
				<#if hasProcedure(component.displayCondition)>
					if (<@procedureOBJToConditionCode component.displayCondition/>)
				</#if>
				${JavaModName}Screens.renderEntityInInventoryFollowsAngle(guiGraphics, this.leftPos + ${x + 10}, this.topPos + ${y + 20}, ${component.scale},
					${component.rotationX / 20.0}f <#if followMouse> + (float) Math.atan((this.leftPos + ${x + 10} - mouseX) / 40.0)</#if>,
					<#if followMouse>(float) Math.atan((this.topPos + ${y + 21 - 50} - mouseY) / 40.0)<#else>0</#if>, livingEntity);
			}
		</#list>
		</#compress>

		this.renderTooltip(guiGraphics, mouseX, mouseY);


		<#list data.getComponentsOfType("Tooltip") as component>
			<#assign x = component.gx(data.width)>
			<#assign y = component.gy(data.height)>
			<#if hasProcedure(component.displayCondition)>
				if (<@procedureOBJToConditionCode component.displayCondition/>)
			</#if>
				if (mouseX > leftPos + ${x} && mouseX < leftPos + ${x + component.width} && mouseY > topPos + ${y} && mouseY < topPos + ${y + component.height}) {
					<#if hasProcedure(component.text)>
					String hoverText = <@procedureOBJToStringCode component.text/>;
					if (hoverText != null) {
						guiGraphics.renderComponentTooltip(font, Arrays.stream(hoverText.split("\n")).map(Component::literal).collect(Collectors.toList()), mouseX, mouseY);
					}
					<#else>
						guiGraphics.renderTooltip(font, Component.translatable("gui.${modid}.${registryname}.${component.getName()}"), mouseX, mouseY);
					</#if>
				}
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
				guiGraphics.blit(ResourceLocation.parse("${modid}:textures/screens/${component.image}"),
					this.leftPos + ${component.gx(data.width)}, this.topPos + ${component.gy(data.height)}, 0, 0,
					${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())},
					${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())});
			<#if hasProcedure(component.displayCondition)>}</#if>
		</#list>

		<#list data.getComponentsOfType("Sprite") as component>
			<#if hasProcedure(component.displayCondition)>if (<@procedureOBJToConditionCode component.displayCondition/>) {</#if>
				guiGraphics.blit(ResourceLocation.parse("${modid}:textures/screens/${component.sprite}"),
					this.leftPos + ${component.gx(data.width)}, this.topPos + ${component.gy(data.height)},
					<#if (component.getTextureWidth(w.getWorkspace()) > component.getTextureHeight(w.getWorkspace()))>
						<@getSpriteByIndex component "width"/>, 0
					<#else>
						0, <@getSpriteByIndex component "height"/>
					</#if>,
					${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())},
					${component.getTextureWidth(w.getWorkspace())}, ${component.getTextureHeight(w.getWorkspace())});
			<#if hasProcedure(component.displayCondition)>}</#if>
		</#list>

		RenderSystem.disableBlend();
	}

	@Override public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}

		<#list textFields as component>
			if(${component.getName()}.isFocused())
				return ${component.getName()}.keyPressed(key, b, c);
		</#list>

		return super.keyPressed(key, b, c);
	}

	<#if sliders?has_content> <#-- AbstractContainerScreen overrides it for slots only, causing a bug with Sliders, so we override it. -->
	@Override
	public boolean mouseDragged(double p_97752_, double p_97753_, int p_97754_, double p_97755_, double p_97756_) {
		return (this.getFocused() != null && this.isDragging() && p_97754_ == 0) ? this.getFocused().mouseDragged(p_97752_, p_97753_, p_97754_, p_97755_, p_97756_) :
				super.mouseDragged(p_97752_, p_97753_, p_97754_, p_97755_, p_97756_);
	}
	</#if>

	<#if textFields?has_content>
	@Override public void resize(Minecraft minecraft, int width, int height) {
		<#list textFields as component>
		String ${component.getName()}Value = ${component.getName()}.getValue();
		</#list>
		super.resize(minecraft, width, height);
		<#list textFields as component>
		${component.getName()}.setValue(${component.getName()}Value);
		</#list>
	}
	</#if>

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

		<#list textFields as component>
			${component.getName()} = new EditBox(this.font, this.leftPos + ${component.gx(data.width) + 1}, this.topPos + ${component.gy(data.height) + 1},
			${component.width - 2}, ${component.height - 2}, Component.translatable("gui.${modid}.${registryname}.${component.getName()}"));
			${component.getName()}.setMaxLength(8192);
			${component.getName()}.setResponder(content -> {
				if (!menuStateUpdateActive)
					menu.sendMenuStateUpdate(entity, 0, "${component.getName()}", content, false);
			});
			<#if component.placeholder?has_content>
			${component.getName()}.setHint(Component.translatable("gui.${modid}.${registryname}.${component.getName()}"));
			</#if>

			this.addWidget(this.${component.getName()});
		</#list>

		<#assign btid = 0>

		<#list buttons as component>
			<#if component.isUndecorated>
				${component.getName()} = new PlainTextButton(
					this.leftPos + ${component.gx(data.width)}, this.topPos + ${component.gy(data.height)},
					${component.width}, ${component.height},
					Component.translatable("gui.${modid}.${registryname}.${component.getName()}"),
					<@buttonOnClick component/>, this.font);
			<#else>
				${component.getName()} = Button.builder(Component.translatable("gui.${modid}.${registryname}.${component.getName()}"), <@buttonOnClick component/>)
					.bounds(this.leftPos + ${component.gx(data.width)}, this.topPos + ${component.gy(data.height)},
					${component.width}, ${component.height}).build();
			</#if>

			this.addRenderableWidget(${component.getName()});

			<#assign btid +=1>
		</#list>

		<#list imageButtons as component>
			${component.getName()} = new ImageButton(
				this.leftPos + ${component.gx(data.width)}, this.topPos + ${component.gy(data.height)},
				${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())},
				<#if component.hoveredImage?has_content>
				new WidgetSprites(ResourceLocation.parse("${modid}:textures/screens/${component.image}"), ResourceLocation.parse("${modid}:textures/screens/${component.hoveredImage}")),
				<#else>
				new WidgetSprites(ResourceLocation.parse("${modid}:textures/screens/${component.image}"), ResourceLocation.parse("${modid}:textures/screens/${component.image}")),
				</#if>
				<@buttonOnClick component/>
			) {
				@Override public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
					<#if hasProcedure(component.displayCondition)>
					if (<@procedureOBJToConditionCode component.displayCondition/>)
					</#if>
					guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
				}
			};

			this.addRenderableWidget(${component.getName()});

			<#assign btid +=1>
		</#list>

		<#list checkboxes as component>
			<#if hasProcedure(component.isCheckedProcedure)>boolean ${component.getName()}Selected = <@procedureOBJToConditionCode component.isCheckedProcedure/>;</#if>
			${component.getName()} = Checkbox.builder(Component.translatable("gui.${modid}.${registryname}.${component.getName()}"), this.font)
				.pos(this.leftPos + ${component.gx(data.width)}, this.topPos + ${component.gy(data.height)})
				.onValueChange((checkbox, value) -> {
					if (!menuStateUpdateActive)
						menu.sendMenuStateUpdate(entity, 1, "${component.getName()}", value, false);
				})
				<#if hasProcedure(component.isCheckedProcedure)>.selected(${component.getName()}Selected)</#if>
				.build();
			<#if hasProcedure(component.isCheckedProcedure)>
				if (${component.getName()}Selected)
					menu.sendMenuStateUpdate(entity, 1, "${component.getName()}", true, false);
			</#if>

			this.addRenderableWidget(${component.getName()});
		</#list>

		<#list sliders as component>
			${component.getName()} = new ExtendedSlider(this.leftPos + ${component.gx(data.width)}, this.topPos + ${component.gy(data.height)},
				${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())}, Component.translatable(
				"gui.${modid}.${registryname}.${component.getName()}_prefix"), Component.translatable("gui.${modid}.${registryname}.${component.getName()}_suffix"),
				${component.min}, ${component.max}, ${component.value}, ${component.step}, 0, true) {
					<#if hasProcedure(component.whenSliderMoves)>
						@Override
						protected void applyValue() {
							PacketDistributor.sendToServer(new ${name}SliderMessage(${btid}, x, y, z, this.getValue()));
							${name}SliderMessage.handleSliderAction(entity, ${btid}, x, y, z, this.getValue());
						}
					</#if>
				};

			this.addRenderableWidget(${component.getName()});

			<#assign btid +=1>
		</#list>
	}

	<#if data.getComponentsOfType("Button")?filter(component -> hasProcedure(component.displayCondition))?size != 0>
	@Override protected void containerTick() {
		super.containerTick();

		<#list data.getComponentsOfType("Button") as component>
			<#if hasProcedure(component.displayCondition)>
				this.${component.getName()}.visible = <@procedureOBJToConditionCode component.displayCondition/>;
			</#if>
		</#list>
	}
	</#if>

}
</#compress>

<#macro buttonOnClick component>
e -> {
	<#if hasProcedure(component.onClick)>
		if (<@procedureOBJToConditionCode component.displayCondition/>) {
			PacketDistributor.sendToServer(new ${name}ButtonMessage(${btid}, x, y, z));
			${name}ButtonMessage.handleButtonAction(entity, ${btid}, x, y, z);
		}
	</#if>
}
</#macro>

<#macro getSpriteByIndex component dim>
	<#if hasProcedure(component.spriteIndex)>
		Mth.clamp((int) <@procedureOBJToNumberCode component.spriteIndex/> *
			<#if dim == "width">
				${component.getWidth(w.getWorkspace())}
			<#else>
				${component.getHeight(w.getWorkspace())}
			</#if>,
			0,
			<#if dim == "width">
				${component.getTextureWidth(w.getWorkspace()) - component.getWidth(w.getWorkspace())}
			<#else>
				${component.getTextureHeight(w.getWorkspace()) - component.getHeight(w.getWorkspace())}
			</#if>
		)
	<#else>
		<#if dim == "width">
			${component.getWidth(w.getWorkspace()) * component.spriteIndex.getFixedValue()}
		<#else>
			${component.getHeight(w.getWorkspace()) * component.spriteIndex.getFixedValue()}
		</#if>
	</#if>
</#macro>
<#-- @formatter:on -->
