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

package ${package}.world;

import net.minecraft.util.SoundEvent;
import javax.annotation.Nullable;

@${JavaModName}Elements.ModElement.Tag
public class ${name}Profession extends ${JavaModName}Elements.ModElement {

    @ObjectHolder("${modid}:${registryname}")
    public static PointOfInterestType pointOfInterest = null;

    @ObjectHolder("${modid}:${registryname}")
    public static final VillagerProfession profession = null;

    public ${name}Profession(${JavaModName}Elements instance) {
        super(instance, ${data.getModElement().getSortID()});
    }

    @SubscribeEvent public static void registerPointOfInterest(RegistryEvent.Register<PointOfInterestType> event) {
        pointOfInterest = new PointOfInterestType("${data.displayName?lower_case}", getAllStates(${mappedBlockToBlock(data.pointOfInterest)}), 1, 1);
        event.getRegistry().register(pointOfInterest);
    }

    @SubscribeEvent public static void registerProfession(RegistryEvent.Register<VillagerProfession> event) {
        event.getRegistry().register(new ProfessionCustom("${modid}" + ":" + "${data.displayName?lower_case}", pointOfInterest, ImmutableSet.of(), ImmutableSet.of(), () -> new SoundEvent(new ResourceLocation("${data.actionSound}"))));
    }

    private static Set<BlockState> getAllStates(Block block) {
        return ImmutableSet.copyOf(block.getStateContainer().getValidStates());
    }

    public static class ProfessionCustom extends VillagerProfession {

        private final List<Supplier<SoundEvent>> soundEventSuppliers;

        @SafeVarargs
        public ProfessionCustom(String name, PointOfInterestType pointOfInterest, ImmutableSet<Item> specificItems, ImmutableSet<Block> relatedWorldBlocks, Supplier<SoundEvent>... soundEventSuppliers) {
            super(name, pointOfInterest, specificItems, relatedWorldBlocks, null);
            this.soundEventSuppliers = Arrays.asList(soundEventSuppliers);
        }

        @Nullable
        @Override
        public SoundEvent getSound() {
            int n = ThreadLocalRandom.current().nextInt(soundEventSuppliers.size());
            return soundEventSuppliers.get(n).get();
        }
    }
}