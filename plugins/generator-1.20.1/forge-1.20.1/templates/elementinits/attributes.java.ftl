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

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ${JavaModName}Attributes {

	public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ${JavaModName}.MODID);

	<#list attributes as attribute>
	public static final RegistryObject<Attribute> ${attribute.getModElement().getRegistryNameUpper()} = REGISTRY.register("${attribute.getModElement().getRegistryName()}",
		() -> new RangedAttribute("attribute.${modid}.${attribute.getModElement().getRegistryName()}", ${attribute.defaultValue}, ${attribute.minValue}, ${attribute.maxValue}).setSyncable(true));
	</#list>

	@SubscribeEvent public static void addAttributes(EntityAttributeModificationEvent event) {
		<#list attributes as attribute>
			<#if attribute.addToAllEntities>
				event.getTypes().forEach(entity -> event.add(entity, ${attribute.getModElement().getRegistryNameUpper()}.get()));
			<#else>
				<#if attribute.entities?has_content>
					List.of(
					<#list attribute.entities as entity>
						${generator.map(entity.getUnmappedValue(), "entities", 1)}<#sep>,
					</#list>
					).stream()
					.filter(DefaultAttributes::hasSupplier)
					.map(entityType -> (EntityType<? extends LivingEntity>) entityType)
					.collect(Collectors.toList()).forEach(entity -> event.add(entity, ${attribute.getModElement().getRegistryNameUpper()}.get()));
				</#if>
				<#if attribute.addToPlayers>
					event.add(EntityType.PLAYER, ${attribute.getModElement().getRegistryNameUpper()}.get());
				</#if>
			</#if>
		</#list>
	}

	<#assign playerAttributes = attributes?filter(a -> a.addToPlayers || a.addToAllEntities)>
	<#if playerAttributes?size != 0>
	@Mod.EventBusSubscriber public static class PlayerAttributesSync {
		@SubscribeEvent public static void playerClone(PlayerEvent.Clone event) {
			Player oldPlayer = event.getOriginal();
			Player newPlayer = event.getEntity();
			<#list playerAttributes as attribute>
				newPlayer.getAttribute(${attribute.getModElement().getRegistryNameUpper()}.get()).setBaseValue(oldPlayer.getAttribute(${attribute.getModElement().getRegistryNameUpper()}.get()).getBaseValue());
			</#list>
		}
	}
	</#if>

}
<#-- @formatter:on -->