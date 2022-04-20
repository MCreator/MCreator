<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2022, Pylo, opensource contributors
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
<#include "mcitems.ftl">

package ${package}.village;

import net.minecraft.sounds.SoundEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${name}Profession {

    public static final DeferredRegister<PoiType> POI = DeferredRegister.create(ForgeRegistries.POI_TYPES, ${JavaModName}.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, ${JavaModName}.MODID);
    public static final RegistryObject<PoiType> ${data.name}_POI = POI.register("${data.id}", () -> new PoiType("${data.id}", getAllStates(${mappedBlockToBlock(data.pointOfInterest)}), 1, 1));
    public static final RegistryObject<VillagerProfession> ${data.name} = registerProfession("${data.id}", ${name}Profession.${data.name}_POI);

    @SuppressWarnings("SameParameterValue")
    private static RegistryObject<VillagerProfession> registerProfession(String name, Supplier<PoiType> poiType) {
        return PROFESSIONS.register(name, () -> new ${JavaModName}VillagerProfessions(${JavaModName}.MODID + ":" + name, poiType.get(), ImmutableSet.of(), ImmutableSet.of(), () -> new SoundEvent(new ResourceLocation("${data.actionSound}"))));
    }

    private static Set<BlockState> getAllStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    @SubscribeEvent public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> ${JavaModName}VillagerProfessions.fixup(${name}Profession.${data.name}_POI.get()));
    }
}