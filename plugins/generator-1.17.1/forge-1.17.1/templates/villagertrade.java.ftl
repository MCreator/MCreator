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
<#include "mcitems.ftl">

package ${package}.village;

import net.minecraft.entity.merchant.villager.VillagerTrades;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE) public class ${name}Trade {

	@SubscribeEvent public static void registerTrades(VillagerTradesEvent event) {
		Int2ObjectMap<List<VillagerTrades.ITrade>> trades = event.getTrades();

    	<#list data.tradeEntries as tradeEntry>
		if (event.getType() == ${tradeEntry.tradeEntry}) {
        	<#list tradeEntry.entries as entry>
			trades.get(${entry.level}).add(new RandomTradeBuilder(${entry.maxTrades}, ${entry.xp}, ${entry.priceMultiplier}F)
					.setPrice(${mappedMCItemToItem(entry.price1)}, ${entry.countPrice1}, ${entry.countPrice1})
					<#if entry.price2 != "">
					.setPrice2(${mappedMCItemToItem(entry.price2)}, ${entry.countPrice2}, ${entry.countPrice2})
					</#if>
					.setForSale(${mappedMCItemToItem(entry.sale1)}, ${entry.countSale1}, ${entry.countSale1})
					.build()
			);
        	</#list>
		}
    	</#list>
	}


	public static class RandomTradeBuilder {

		private Function<Random, ItemStack> price;
		private Function<Random, ItemStack> price2;
		private Function<Random, ItemStack> forSale;
		private final int maxTrades;
		private final int xp;
		private final float priceMult;

		public RandomTradeBuilder(int maxTrades, int xp, float priceMult) {
			this.price = null;
			this.price2 = (random) -> ItemStack.EMPTY;
			this.forSale = null;
			this.maxTrades = maxTrades;
			this.xp = xp;
			this.priceMult = priceMult;
		}

		public RandomTradeBuilder setPrice(Function<Random, ItemStack> price) {
			this.price = price;
			return this;
		}

		public RandomTradeBuilder setPrice(Item item, int min, int max) {
			return this.setPrice(RandomTradeBuilder.createFunction(item, min, max));
		}

		public RandomTradeBuilder setPrice2(Function<Random, ItemStack> price2) {
			this.price2 = price2;
			return this;
		}

		public RandomTradeBuilder setPrice2(Item item, int min, int max) {
			return this.setPrice2(RandomTradeBuilder.createFunction(item, min, max));
		}

		public RandomTradeBuilder setForSale(Function<Random, ItemStack> forSale) {
			this.forSale = forSale;
			return this;
		}

		public RandomTradeBuilder setForSale(Item item, int min, int max) {
			return this.setForSale(RandomTradeBuilder.createFunction(item, min, max));
		}

		public boolean canBuild() {
			return this.price != null && this.forSale != null;
		}

		public VillagerTrades.ITrade build() {
			return (entity, random) -> !this.canBuild()
			? null
			: new MerchantOffer(this.price.apply(random), this.price2.apply(random), this.forSale.apply(random), this.maxTrades, this.xp,
			this.priceMult);
		}

		public static Function<Random, ItemStack> createFunction(Item item, int min, int max) {
			return (random) -> new ItemStack(item, max);
		}
	}
}
