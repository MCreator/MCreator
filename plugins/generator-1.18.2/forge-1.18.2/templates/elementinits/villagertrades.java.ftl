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

import net.minecraft.world.entity.npc.VillagerTrades;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE) public class ${JavaModName}Trades {

    @SubscribeEvent public static void registerTrades(VillagerTradesEvent event) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

        <#list villagertrades as trade>
        <#list trade.tradeEntries as tradeEntry>
            if (event.getType() == ${tradeEntry.tradeEntry}) {
            <#list tradeEntry.entries as entry>
                trades.get(${entry.level}).add(new BasicItemListing(${mappedMCItemToItemStackCode(entry.price1, entry.countPrice1)},
                <#if entry.price2 != "">
                    ${mappedMCItemToItemStackCode(entry.price2, entry.countPrice2)},
                </#if>
                ${mappedMCItemToItemStackCode(entry.sale, entry.countSale)}, ${entry.maxTrades}, ${entry.xp}, ${entry.priceMultiplier}F));
            </#list>
            }
        </#list>
        </#list>
    }
}
