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

package ${package}.village;

public class ${JavaModName}RandomTradeBuilder {

		private Function<Random, ItemStack> price;
		private Function<Random, ItemStack> price2;
		private Function<Random, ItemStack> forSale;
		private final int maxTrades;
		private final int xp;
		private final float priceMult;

		public ${JavaModName}RandomTradeBuilder(int maxTrades, int xp, float priceMult) {
			this.price = null;
			this.price2 = (random) -> ItemStack.EMPTY;
			this.forSale = null;
			this.maxTrades = maxTrades;
			this.xp = xp;
			this.priceMult = priceMult;
		}

		public ${JavaModName}RandomTradeBuilder setPrice(Function<Random, ItemStack> price) {
			this.price = price;
			return this;
		}

		public ${JavaModName}RandomTradeBuilder setPrice(Item item, int min, int max) {
			return this.setPrice(${JavaModName}RandomTradeBuilder.createFunction(item, min, max));
		}

		public ${JavaModName}RandomTradeBuilder setPrice2(Function<Random, ItemStack> price2) {
			this.price2 = price2;
			return this;
		}

		public ${JavaModName}RandomTradeBuilder setPrice2(Item item, int min, int max) {
			return this.setPrice2(${JavaModName}RandomTradeBuilder.createFunction(item, min, max));
		}

		public ${JavaModName}RandomTradeBuilder setForSale(Function<Random, ItemStack> forSale) {
			this.forSale = forSale;
			return this;
		}

		public ${JavaModName}RandomTradeBuilder setForSale(Item item, int min, int max) {
			return this.setForSale(${JavaModName}RandomTradeBuilder.createFunction(item, min, max));
		}

		public boolean canBuild() {
			return this.price != null && this.forSale != null;
		}

		public VillagerTrades.ItemListing build() {
			return (entity, random) -> !this.canBuild()
			? null
			: new MerchantOffer(this.price.apply(random), this.price2.apply(random), this.forSale.apply(random), this.maxTrades, this.xp,
			this.priceMult);
		}

		public static Function<Random, ItemStack> createFunction(Item item, int min, int max) {
			return (random) -> new ItemStack(item, max);
		}
}
