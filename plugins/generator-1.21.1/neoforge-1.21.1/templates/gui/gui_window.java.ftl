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

<#assign hasEntityModels = false>

<#assign textFields = data.getComponentsOfType("TextField")>
<#assign checkboxes = data.getComponentsOfType("Checkbox")>
<#assign buttons = data.getComponentsOfType("Button")>
<#assign imageButtons = data.getComponentsOfType("ImageButton")>

public class ${name}Screen extends AbstractContainerScreen<${name}Menu> implements ${JavaModName}Screens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	<#if data.getComponentsOfType("Checkbox")?has_content>private boolean updateLock;</#if>

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

	public void onMenuStateUpdate(int elementType, String name, Object elementState) {
		<#if data.getComponentsOfType("TextField")?has_content>
		if (elementType == 0 && elementState instanceof String stringState) {
			<#list data.getComponentsOfType("TextField") as component>
				if (name.equals("${component.getName()}")) {
					${component.getName()}.setValue(stringState);
				}
			</#list>
		}
		</#if>
		<#if data.getComponentsOfType("Checkbox")?has_content>
		if (elementType == 1 && elementState instanceof Boolean logicState) {
			this.updateLock = true;
			<#list data.getComponentsOfType("Checkbox") as component>
				if (name.equals("${component.getName()}") && logicState.booleanValue() != ${component.getName()}.selected()) {
					${component.getName()}.onPress();
				}
			</#list>
			this.updateLock = false;
		}
		</#if>
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

		<#list data.getComponentsOfType("EntityModel") as component>
			<#assign hasEntityModels = true>
			<#assign followMouse = component.followMouseMovement>
			<#assign x = component.gx(data.width)>
			<#assign y = component.gy(data.height)>
			if (<@procedureOBJToConditionCode component.entityModel/> instanceof LivingEntity livingEntity) {
				<#if hasProcedure(component.displayCondition)>
					if (<@procedureOBJToConditionCode component.displayCondition/>)
				</#if>
				this.renderEntityInInventoryFollowsAngle(guiGraphics, this.leftPos + ${x + 10}, this.topPos + ${y + 20}, ${component.scale},
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
			${component.getName()} = ${JavaModName}Screens.createListenerTextField(this.font, this.leftPos + ${component.gx(data.width) + 1}, this.topPos + ${component.gy(data.height) + 1},
			${component.width - 2}, ${component.height - 2}, Component.translatable("gui.${modid}.${registryname}.${component.getName()}"), (content) -> {
				menu.sendMenuStateUpdate(world, 0, "${component.getName()}", content);
			}, <#if component.placeholder?has_content> true <#else> false </#if>);
			${component.getName()}.setMaxLength(32767);
			<#if component.placeholder?has_content>
			${component.getName()}.setSuggestion(Component.translatable("gui.${modid}.${registryname}.${component.getName()}").getString());
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
					if (!this.updateLock)
						menu.sendMenuStateUpdate(world, 1, "${component.getName()}", value);
				})
				<#if hasProcedure(component.isCheckedProcedure)>.selected(${component.getName()}Selected)</#if>
				.build();
			<#if hasProcedure(component.isCheckedProcedure)>
				if (${component.getName()}Selected)
					menu.sendMenuStateUpdate(world, 1, "${component.getName()}", true);
			</#if>

			this.addRenderableWidget(${component.getName()});
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

	<#if hasEntityModels>
	private void renderEntityInInventoryFollowsAngle(GuiGraphics guiGraphics, int x, int y, int scale, float angleXComponent, float angleYComponent, LivingEntity entity) {
		Quaternionf pose = new Quaternionf().rotateZ((float)Math.PI);
		Quaternionf cameraOrientation = new Quaternionf().rotateX(angleYComponent * 20 * ((float) Math.PI / 180F));
		pose.mul(cameraOrientation);
		float f2 = entity.yBodyRot;
		float f3 = entity.getYRot();
		float f4 = entity.getXRot();
		float f5 = entity.yHeadRotO;
		float f6 = entity.yHeadRot;
		entity.yBodyRot = 180.0F + angleXComponent * 20.0F;
		entity.setYRot(180.0F + angleXComponent * 40.0F);
		entity.setXRot(-angleYComponent * 20.0F);
		entity.yHeadRot = entity.getYRot();
		entity.yHeadRotO = entity.getYRot();
		InventoryScreen.renderEntityInInventory(guiGraphics, x, y, scale, new Vector3f(0, 0, 0), pose, cameraOrientation, entity);
		entity.yBodyRot = f2;
		entity.setYRot(f3);
		entity.setXRot(f4);
		entity.yHeadRotO = f5;
		entity.yHeadRot = f6;
	}
	</#if>

}

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