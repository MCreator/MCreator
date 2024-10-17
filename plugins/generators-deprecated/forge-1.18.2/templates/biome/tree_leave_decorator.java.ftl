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

public class ${name}LeaveDecorator extends LeaveVineDecorator {

        public static final ${name}LeaveDecorator INSTANCE = new ${name}LeaveDecorator();

        public static com.mojang.serialization.Codec<LeaveVineDecorator> codec;
        public static TreeDecoratorType<?> tdt;

        static {
            codec = com.mojang.serialization.Codec.unit(() -> INSTANCE);
            tdt = new TreeDecoratorType<>(codec);
            tdt.setRegistryName("${registryname}_tree_leave_decorator");
            ForgeRegistries.TREE_DECORATOR_TYPES.register(tdt);
        }

        @Override
        protected TreeDecoratorType<?> type() {
            return tdt;
        }

        @Override
        public void place(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> biConsumer, Random random, List<BlockPos> listBlockPos, List<BlockPos> listBlockPos2) {
            listBlockPos2.forEach((blockpos) -> {
                if (random.nextInt(4) == 0) {
                    BlockPos bp = blockpos.below();
                    if (Feature.isAir(level, bp)) {
                        addVine(level, bp, biConsumer);
                    }
                }

            });
        }

        private static void addVine(LevelSimulatedReader levelReader, BlockPos blockPos, BiConsumer<BlockPos, BlockState> biConsumer) {
            biConsumer.accept(blockPos, ${mappedBlockToBlockStateCode(data.treeVines)});
            int i = 4;
            for(BlockPos blockpos = blockPos.below(); Feature.isAir(levelReader, blockpos) && i > 0; --i) {
                biConsumer.accept(blockpos, ${mappedBlockToBlockStateCode(data.treeVines)});
                blockpos = blockpos.below();
            }

        }

}
<#-- @formatter:on -->
