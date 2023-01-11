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

<#include "../mcitems.ftl">

/*
*    MCreator note: This file will be REGENERATED on each build.
*/

package ${package}.init;

import net.minecraft.sounds.SoundEvent;
import javax.annotation.Nullable;

public class ${JavaModName}VillagerProfessions {

    public static final DeferredRegister<PoiType> POI = DeferredRegister.create(ForgeRegistries.POI_TYPES, ${JavaModName}.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, ${JavaModName}.MODID);

    <#list villagerprofessions as villagerprofession>
        public static final RegistryObject<PoiType> ${villagerprofession.getModElement().getRegistryNameUpper()}_POI = POI.register("${villagerprofession.getModElement().getRegistryName()}", () -> new PoiType("${villagerprofession.getModElement().getRegistryName()}", getAllStates(${mappedBlockToBlock(villagerprofession.pointOfInterest)}), 1, 1));
        public static final RegistryObject<VillagerProfession> ${villagerprofession.getModElement().getRegistryNameUpper()} = registerProfession("${villagerprofession.getModElement().getRegistryName()}", ${villagerprofession.getModElement().getRegistryNameUpper()}_POI, () -> new SoundEvent(new ResourceLocation("${villagerprofession.actionSound}")));
    </#list>

    private static RegistryObject<VillagerProfession> registerProfession(String name, Supplier<PoiType> poiType, Supplier<SoundEvent>... soundEventSuppliers) {
        return PROFESSIONS.register(name, () -> new CustomVillagerProfession(${JavaModName}.MODID + ":" + name, poiType.get(), ImmutableSet.of(), ImmutableSet.of(), soundEventSuppliers));
    }

    private static Set<BlockState> getAllStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    public static class CustomVillagerProfession extends VillagerProfession {

        private final List<Supplier<SoundEvent>> soundEventSuppliers;

        @SafeVarargs
        public CustomVillagerProfession(String name, PoiType pointOfInterest, ImmutableSet<Item> specificItems, ImmutableSet<Block> relatedWorldBlocks, Supplier<SoundEvent>... soundEventSuppliers) {
            super(name, pointOfInterest, specificItems, relatedWorldBlocks, null);
            this.soundEventSuppliers = Arrays.asList(soundEventSuppliers);
        }

        @Nullable
        @Override
        public SoundEvent getWorkSound() {
            int n = ThreadLocalRandom.current().nextInt(soundEventSuppliers.size());
            return soundEventSuppliers.get(n).get();
        }
    }
}
<#-- @formatter:on -->