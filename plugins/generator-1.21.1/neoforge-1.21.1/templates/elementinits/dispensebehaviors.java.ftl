<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2025, Pylo, opensource contributors
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
<#include "../procedures.java.ftl">

/*
 *	MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<@javacompress>
@EventBusSubscriber public class ${JavaModName}DispenseBehaviors {

	@SubscribeEvent public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			<#list itemextensions?filter(e -> e.hasDispenseBehavior) as extension>
			DispenserBlock.registerBehavior(${mappedMCItemToItem(extension.item)}, new OptionalDispenseItemBehavior() {
				public ItemStack execute(BlockSource blockSource, ItemStack stack) {
					<#assign hasSuccessCondition = hasProcedure(extension.dispenseSuccessCondition)>
					ItemStack itemstack = stack.copy();
					Level world = blockSource.level();
					Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
					int x = blockSource.pos().getX();
					int y = blockSource.pos().getY();
					int z = blockSource.pos().getZ();

					<#if hasSuccessCondition>
						this.setSuccess(<@procedureOBJToConditionCode extension.dispenseSuccessCondition/>);
					</#if>

					<#if hasProcedure(extension.dispenseResultItemstack)>
						boolean success = this.isSuccess();
						<#if hasReturnValueOf(extension.dispenseResultItemstack, "itemstack")>
							return <@procedureOBJToItemstackCode extension.dispenseResultItemstack, false/>;
						<#else>
							<@procedureOBJToCode extension.dispenseResultItemstack/>
							<#if hasSuccessCondition>if(success)</#if>
							itemstack.shrink(1);
							return itemstack;
						</#if>
					<#else>
						<#if hasSuccessCondition>if(this.isSuccess())</#if>
						itemstack.shrink(1);
						return itemstack;
					</#if>
				}
			});
			</#list>
		});
	}

}</@javacompress>