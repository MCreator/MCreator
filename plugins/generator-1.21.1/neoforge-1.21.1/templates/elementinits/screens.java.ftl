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

<#assign hasTextField = false>

package ${package}.init;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT) public class ${JavaModName}Screens {

	@SubscribeEvent public static void clientLoad(RegisterMenuScreensEvent event) {
		<#list guis as gui>
		event.register(${JavaModName}Menus.${gui.getModElement().getRegistryNameUpper()}.get(), ${gui.getModElement().getName()}Screen::new);
		<#if gui.getComponentsOfType("TextField")?has_content>
            <#assign hasTextField = true>
        </#if>
		</#list>
	}

	public static void onGuistateUpdate(int elementType, String name, Object elementState) {
	    if (Minecraft.getInstance().screen instanceof ScreenAccessor accessor) {
	        accessor.onGuistateUpdate(elementType, name, elementState);
	    }
	}

	public interface ScreenAccessor {
	    void onGuistateUpdate(int elementType, String name, Object elementState);
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

}

<#-- @formatter:on -->