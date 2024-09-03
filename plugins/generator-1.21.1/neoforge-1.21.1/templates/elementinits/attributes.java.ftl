<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2024, Pylo, opensource contributors
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
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

@EventBusSubscriber (bus = EventBusSubscriber.Bus.MOD)
public class ${JavaModName}Attributes {
	public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, ${JavaModName}.MODID);

	<#list attributes as attribute>
		public static final DeferredHolder<Attribute, Attribute> ${attribute.getModElement().getRegistryNameUpper()} = REGISTRY.register("${attribute.getModElement().getRegistryName()}", () -> (new RangedAttribute("attribute.${modid}.${attribute.getModElement().getRegistryName()}", ${attribute.defaultValue}, ${attribute.minValue}, ${attribute.maxValue})).setSyncable(true));
	</#list>

	@SubscribeEvent
	public static void addAttributes(EntityAttributeModificationEvent event) {
		<#list attributes as attribute>
			<#assign condition = "">
			<#list attribute.entities as entity>
				<#if entity.getUnmappedValue() != "EntityZombie" && generator.map(entity.getUnmappedValue(), "entities", 1) == "EntityType.ZOMBIE">
					<#assign condition += "|| baseClass.isAssignableFrom(${entity}.class)">
				<#else>
					event.add(${generator.map(entity.getUnmappedValue(), "entities", 1)}, ${attribute.getModElement().getRegistryNameUpper()}.getDelegate());
				</#if>
			</#list>
			<#if condition != "">
				event.getTypes().forEach((e) -> {
					Class<? extends Entity> baseClass = e.getBaseClass();
					if(${condition?keep_after("|| ")}) {
						event.add(e, ${attribute.getModElement().getRegistryNameUpper()}.getDelegate());
					}
				});
			</#if>
		</#list>
	}

}
<#-- @formatter:on -->