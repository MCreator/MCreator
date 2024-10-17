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
 # along setValue this program.  If not, see <https://www.gnu.org/licenses/>.
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
package ${package}.world.features.treedecorators;
<#include "../mcitems.ftl">

public class ${name}FruitDecorator extends CocoaDecorator {

    public static final ${name}FruitDecorator INSTANCE = new ${name}FruitDecorator();

    public static com.mojang.serialization.Codec<${name}FruitDecorator> codec;
    public static TreeDecoratorType<?> tdt;

    static {
        codec = com.mojang.serialization.Codec.unit(() -> INSTANCE);
        tdt = new TreeDecoratorType<>(codec);
        tdt.setRegistryName("${registryname}_tree_fruit_decorator");
        ForgeRegistries.TREE_DECORATOR_TYPES.register(tdt);
    }

    public ${name}FruitDecorator() {
        super(0.2f);
    }

    @Override protected TreeDecoratorType<?> type() {
        return tdt;
    }

    @Override ${mcc.getMethod("net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator", "place", "LevelSimulatedReader", "java.util.function.BiConsumer<BlockPos, BlockState>", "Random", "List", "List")
    .replace("this.probability", "0.2F")
    .replace("Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE,Integer.valueOf(p_161721_.nextInt(3))).setValue(CocoaBlock.FACING,direction)",
        mappedBlockToBlockStateCode(data.treeFruits))
    .replace("p_161719_", "level")
    .replace("p_161720_", "biConsumer")
    .replace("p_161721_", "random")
    .replace("p_161722_", "blocks")
    .replace("p_161723_", "blocks2")}

}
<#-- @formatter:on -->
