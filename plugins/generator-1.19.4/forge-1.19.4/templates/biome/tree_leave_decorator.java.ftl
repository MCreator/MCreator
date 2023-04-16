<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2023, Pylo, opensource contributors
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

<#include "../mcitems.ftl">

package ${package}.world.features.treedecorators;

import com.mojang.serialization.Codec;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${name}LeaveDecorator extends LeaveVineDecorator {

    public static Codec<LeaveVineDecorator> CODEC = Codec.unit(${name}LeaveDecorator::new);

    public static TreeDecoratorType<?> DECORATOR_TYPE = new TreeDecoratorType<>(CODEC);

	@SubscribeEvent public static void registerPointOfInterest(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.TREE_DECORATOR_TYPES, registerHelper -> registerHelper.register("${registryname}_tree_leave_decorator", DECORATOR_TYPE));
	}

	public ${name}LeaveDecorator() {
		super(0.25f);
	}

    @Override
    protected TreeDecoratorType<?> type() {
        return DECORATOR_TYPE;
    }

    @Override
    public void place(TreeDecorator.Context context) {
        context.leaves().forEach((blockpos) -> {
            if (context.random().nextFloat() <  0.25f) {
                BlockPos pos = blockpos.west();
                if (context.isAir(pos)) {
                    addVine(pos, context);
                }
            }

			if (context.random().nextFloat() <  0.25f) {
				BlockPos pos = blockpos.east();
				if (context.isAir(pos)) {
					addVine(pos, context);
				}
			}

			if (context.random().nextFloat() <  0.25f) {
				BlockPos pos = blockpos.north();
				if (context.isAir(pos)) {
					addVine(pos, context);
				}
			}

			if (context.random().nextFloat() <  0.25f) {
				BlockPos pos = blockpos.south();
				if (context.isAir(pos)) {
					addVine(pos, context);
				}
			}
        });
    }

    private static void addVine(BlockPos pos, TreeDecorator.Context context) {
		context.setBlock(pos, ${mappedBlockToBlockStateCode(data.treeVines)});
        int i = 4;
        for(BlockPos blockpos = pos.below(); context.isAir(blockpos) && i > 0; --i) {
			context.setBlock(blockpos, ${mappedBlockToBlockStateCode(data.treeVines)});
            blockpos = blockpos.below();
        }

    }

}
<#-- @formatter:on -->
