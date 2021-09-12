<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2021, Pylo, opensource contributors
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
package ${package}.world;

import ${package}.${JavaModName};

@${JavaModName}Elements.ModElement.Tag
public class ${name}GameRule extends ${JavaModName}Elements.ModElement {

    <#if data.type == "Number">
    public static final GameRules.Key<GameRules.IntegerValue> gamerule = GameRules.register("${registryname}", GameRules.Category.${data.category}, create(${data.defaultValueNumber}));
    <#else>
    public static final GameRules.Key<GameRules.BooleanValue> gamerule = GameRules.register("${registryname}", GameRules.Category.${data.category}, create(${data.defaultValueLogic}));
    </#if>

    public ${name}GameRule (${JavaModName}Elements instance) {
        super(instance, ${data.getModElement().getSortID()});
    }

    <#if data.type == "Number">
        public static GameRules.Type<GameRules.IntegerValue> create(int defaultValue) {
            try {
                Method createGameruleMethod = ObfuscationReflectionHelper.findMethod(GameRules.IntegerValue.class, "m_46312_", int.class);
                createGameruleMethod.setAccessible(true);
                return (GameRules.Type<GameRules.IntegerValue>) createGameruleMethod.invoke(null, defaultValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    <#else>
        public static GameRules.Type<GameRules.BooleanValue> create(boolean defaultValue) {
            try {
                Method createGameruleMethod = ObfuscationReflectionHelper.findMethod(GameRules.BooleanValue.class, "m_46250_", boolean.class);
                createGameruleMethod.setAccessible(true);
                return (GameRules.Type<GameRules.BooleanValue>) createGameruleMethod.invoke(null, defaultValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    </#if>
}
<#-- @formatter:on -->