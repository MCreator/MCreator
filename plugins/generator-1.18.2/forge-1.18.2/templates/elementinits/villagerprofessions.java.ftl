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

/*
*    MCreator note: This file will be REGENERATED on each build.
*/

package ${package}.init;

import net.minecraft.sounds.SoundEvent;
import javax.annotation.Nullable;

public class ${JavaModName}VillagerProfessions extends VillagerProfession {

    private static final Method blockStatesInjector = ObfuscationReflectionHelper.findMethod(PoiType.class, "m_27367_", PoiType.class);
    private final List<Supplier<SoundEvent>> soundEventSuppliers;

    @SafeVarargs
    public ${JavaModName}VillagerProfessions(String name, PoiType pointOfInterest, ImmutableSet<Item> specificItems, ImmutableSet<Block> relatedWorldBlocks, Supplier<SoundEvent>... soundEventSuppliers) {
        super(name, pointOfInterest, specificItems, relatedWorldBlocks, null);
        this.soundEventSuppliers = Arrays.asList(soundEventSuppliers);
    }

    @Nullable
    @Override
    public SoundEvent getWorkSound() {
        int n = ThreadLocalRandom.current().nextInt(soundEventSuppliers.size());
        return soundEventSuppliers.get(n).get();
    }

    public static void fixup(PoiType poiType) {
        try {
            blockStatesInjector.invoke(null, poiType);
        } catch (IllegalAccessException | InvocationTargetException e) {
            ${JavaModName}.LOGGER.catching(e);
        }
    }
}