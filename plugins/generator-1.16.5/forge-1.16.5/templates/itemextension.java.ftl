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
<#include "procedures.java.ftl">

package ${package}.item.extension;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${name}ItemExtension {

	<#if data.hasDispenseBehavior || data.layerChance gt 0>
		@SubscribeEvent
		public void init(FMLCommonSetupEvent event) {
		    <#if data.layerChance gt 0>
		        ComposterBlock.CHANCES.put(${mappedMCItemToItem(data.item)}, ${data.layerChance}f);
		    </#if>

		    <#if data.hasDispenseBehavior>
                DispenserBlock.registerDispenseBehavior(${mappedMCItemToItem(data.item)}, new OptionalDispenseBehavior() {
                    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack) {
                        ItemStack itemstack = stack.copy();
                        World world = blockSource.getWorld();
                        Direction direction = blockSource.getBlockState().get(DispenserBlock.FACING);
                        int x = blockSource.getBlockPos().getX();
                        int y = blockSource.getBlockPos().getY();
                        int z = blockSource.getBlockPos().getZ();

                        this.setSuccessful(<@procedureOBJToConditionCode data.dispenseSuccessCondition/>);

                        <#if hasProcedure(data.dispenseResultItemstack)>
                            boolean success = this.isSuccessful();
                            <#if hasReturnValueOf(data.dispenseResultItemstack, "itemstack")>
                                return <@procedureOBJToItemstackCode data.dispenseResultItemstack/>;
                            <#else>
                                <@procedureOBJToCode data.dispenseResultItemstack/>
                                if(success) itemstack.shrink(1);
                                return itemstack;
                            </#if>
                        <#else>
                            if(this.isSuccessful()) itemstack.shrink(1);
                            return itemstack;
                        </#if>
                    }
                });
            </#if>
		}
	</#if>

    <#if data.enableFuel>
        @Mod.EventBusSubscriber
        public static class Fuel {
            @SubscribeEvent
            public static void furnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
		        ItemStack itemstack = event.getItemStack();
                if(event.getItemStack().getItem() == ${mappedMCItemToItem(data.item)})
                    <#if hasProcedure(data.fuelSuccessCondition)>if (<@procedureOBJToConditionCode data.fuelSuccessCondition/>)</#if>
                        <#if hasProcedure(extension.fuelPower)>
                            event.setBurnTime((int) <@procedureOBJToNumberCode extension.fuelPower/>);
                        <#else>
                            event.setBurnTime(${extension.fuelPower.getFixedValue()});
                        </#if>
            }
        }
    </#if>
}
<#-- @formatter:on -->