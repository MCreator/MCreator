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

public class ${JavaModName}Menus {

	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, ${JavaModName}.MODID);

	<#list guis as gui>
	public static final DeferredHolder<MenuType<?>, MenuType<${gui.getModElement().getName()}Menu>> ${gui.getModElement().getRegistryNameUpper()}
		= REGISTRY.register("${gui.getModElement().getRegistryName()}", () -> IMenuTypeExtension.create(${gui.getModElement().getName()}Menu::new));
	</#list>

	public interface MenuAccessor {
	    HashMap<String, Object> getMenuState();
	}

	public static void updateMenuState(Player entity, int elementType, String name, Object elementState) {
	    if (entity.containerMenu instanceof MenuAccessor menu) {
        	HashMap<String, Object> menuState = menu.getMenuState();
            if (elementType == 0) {
                menuState.put("textfield:" + name, elementState);
            } else if (elementType == 1) {
                menuState.put("checkbox:" + name, elementState);
            }
        }
	}

	public static void sendMenuStateUpdate(Player entity, int elementType, String name, Object elementState) {
	    /*
	     * There should be a synchronization code here to send data to the opposite side.
	     */
	    updateMenuState(entity, elementType, name, elementState); //This method will also be called from the network packet on the opposite side.
	}

    <#-- At the moment this getter method returns a value only from the called side, it is not synchronized with the opposite side. -->
	public static <T> T getMenuState(Entity entity, int elementType, String name, T defaultValue) {
        if (entity instanceof Player _entity && _entity.containerMenu instanceof MenuAccessor accessor) {
            HashMap<String, Object> menuState = accessor.getMenuState();
            try {
                if (elementType == 0) {
                    return (T) menuState.getOrDefault("textfield:" + name, defaultValue);
                }
                if (elementType == 1) {
                    return (T) menuState.getOrDefault("checkbox:" + name, defaultValue);
                }
            } catch (ClassCastException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

}

<#-- @formatter:on -->