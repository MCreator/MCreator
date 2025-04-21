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

/*
 *	MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#assign hasTextField = false>
<#assign hasEntityModels = false>

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT) public class ${JavaModName}Screens {

	@SubscribeEvent public static void clientLoad(RegisterMenuScreensEvent event) {
		<#list guis as gui>
			<#if gui.getComponentsOfType("EntityModel")?has_content><#assign hasEntityModels = true></#if>
			event.register(${JavaModName}Menus.${gui.getModElement().getRegistryNameUpper()}.get(), ${gui.getModElement().getName()}Screen::new);
			<#if gui.getComponentsOfType("TextField")?has_content><#assign hasTextField = true></#if>
		</#list>
	}

	<#if hasTextField>
	public static EditBox createListenerTextField(Font font, int x, int y, int width, int height, Component narratable, Consumer<String> listener, boolean suggest) {
		return new EditBox(font, x, y, width, height, narratable) {
			@Override public boolean keyPressed(int key, int b, int c) {
				boolean flag = super.keyPressed(key, b, c);
				listener.accept(this.getValue());
				return flag;
			}

			@Override public boolean charTyped(char c, int type) {
				boolean flag = super.charTyped(c, type);
				listener.accept(this.getValue());
				return flag;
			}

			@Override public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty() && suggest)
					setSuggestion(narratable.getString());
				else
					setSuggestion(null);
			}

			@Override public void moveCursorTo(int pos, boolean flag) {
				super.moveCursorTo(pos, flag);
				if (getValue().isEmpty() && suggest)
					setSuggestion(narratable.getString());
				else
					setSuggestion(null);
			}

		};
	}
	</#if>

	public interface ScreenAccessor {
		void onMenuStateUpdate(int elementType, String name, Object elementState);
	}

	<#if hasEntityModels>
	public static void renderEntityInInventoryFollowsAngle(GuiGraphics guiGraphics, int x, int y, int scale, float angleXComponent, float angleYComponent, LivingEntity entity) {
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

<#-- @formatter:on -->