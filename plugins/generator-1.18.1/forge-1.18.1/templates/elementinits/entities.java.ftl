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

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${JavaModName}Entities {

	private static final List<EntityType<?>> REGISTRY = new ArrayList<>();

	<#list entities as entity>
		<#if entity.getModElement().getTypeString() == "rangeditem">
			public static final EntityType<${entity.getModElement().getName()}Entity> ${entity.getModElement().getRegistryNameUpper()} =
						register("entitybullet${entity.getModElement().getRegistryName()}", EntityType.Builder.<${entity.getModElement().getName()}Entity>
							of(${entity.getModElement().getName()}Entity::new, MobCategory.MISC).setCustomClientFactory(${entity.getModElement().getName()}Entity::new)
							.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
		<#else>
			public static final EntityType<${entity.getModElement().getName()}Entity> ${entity.getModElement().getRegistryNameUpper()} =
						register("${entity.getModElement().getRegistryName()}",
								EntityType.Builder.<${entity.getModElement().getName()}Entity>
								of(${entity.getModElement().getName()}Entity::new, ${generator.map(entity.mobSpawningType, "mobspawntypes")})
								.setShouldReceiveVelocityUpdates(true).setTrackingRange(${entity.trackingRange}).setUpdateInterval(3)
								.setCustomClientFactory(${entity.getModElement().getName()}Entity::new)
							<#if entity.immuneToFire>.fireImmune()</#if>.sized(${entity.modelWidth}f, ${entity.modelHeight}f));
			<#if entity.hasCustomProjectile()>
			public static final EntityType<${entity.getModElement().getName()}EntityProjectile> ${entity.getModElement().getRegistryNameUpper()}_PROJECTILE =
					register("entitybullet${entity.getModElement().getRegistryName()}", EntityType.Builder.<${entity.getModElement().getName()}EntityProjectile>
							of(${entity.getModElement().getName()}EntityProjectile::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
							.setUpdateInterval(1).setCustomClientFactory(${entity.getModElement().getName()}EntityProjectile::new).sized(0.5f, 0.5f));
			</#if>
		</#if>
    </#list>

	private static <T extends Entity> EntityType<T> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		EntityType<T> entityType = (EntityType<T>) entityTypeBuilder.build(registryname).setRegistryName(registryname);
		REGISTRY.add(entityType);
		return entityType;
	}

	@SubscribeEvent public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		event.getRegistry().registerAll(REGISTRY.toArray(new EntityType[0]));
	}

	@SubscribeEvent public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
		<#list entities as entity>
			<#if entity.getModElement().getTypeString() == "livingentity">
				${entity.getModElement().getName()}Entity.init();
			</#if>
		</#list>
		});
	}

	@SubscribeEvent public static void registerAttributes(EntityAttributeCreationEvent event) {
		<#list entities as entity>
			<#if entity.getModElement().getTypeString() == "livingentity">
				event.put(${entity.getModElement().getRegistryNameUpper()}, ${entity.getModElement().getName()}Entity.createAttributes().build());
			</#if>
		</#list>
	}

}
<#-- @formatter:on -->