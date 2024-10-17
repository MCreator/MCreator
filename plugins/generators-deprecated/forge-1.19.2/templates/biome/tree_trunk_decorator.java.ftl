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

public class ${name}TrunkDecorator extends TrunkVineDecorator {

    public static final ${name}TrunkDecorator INSTANCE = new ${name}TrunkDecorator();

    public static com.mojang.serialization.Codec<${name}TrunkDecorator> codec;
    public static TreeDecoratorType<?> tdt;

    static {
        codec = com.mojang.serialization.Codec.unit(() -> INSTANCE);
        tdt = new TreeDecoratorType<>(codec);
        ForgeRegistries.TREE_DECORATOR_TYPES.register("${registryname}_tree_trunk_decorator", tdt);
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return tdt;
    }

    @Override
    public void place(TreeDecorator.Context context) {
        context.logs().forEach(blockpos -> {
            if (context.random().nextInt(3) > 0) {
                BlockPos pos = blockpos.west();
                if (context.isAir(pos)) {
					context.setBlock(pos, ${mappedBlockToBlockStateCode(data.treeVines)});
                }
            }

			if (context.random().nextInt(3) > 0) {
				BlockPos pos = blockpos.east();
				if (context.isAir(pos)) {
					context.setBlock(pos, ${mappedBlockToBlockStateCode(data.treeVines)});
				}
			}

			if (context.random().nextInt(3) > 0) {
				BlockPos pos = blockpos.north();
				if (context.isAir(pos)) {
					context.setBlock(pos, ${mappedBlockToBlockStateCode(data.treeVines)});
				}
			}

			if (context.random().nextInt(3) > 0) {
				BlockPos pos = blockpos.south();
				if (context.isAir(pos)) {
					context.setBlock(pos, ${mappedBlockToBlockStateCode(data.treeVines)});
				}
			}

        });
    }

}
<#-- @formatter:on -->
