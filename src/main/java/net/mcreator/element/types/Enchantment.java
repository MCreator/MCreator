/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.element.types;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;

import java.util.List;

@SuppressWarnings("unused") public class Enchantment extends GeneratableElement {

	public String name;
	public String type;
	public String rarity;

	public int maxLevel;

	public int damageModifier;

	@ModElementReference public List<net.mcreator.element.parts.Enchantment> compatibleEnchantments;
	public boolean excludeEnchantments;

	@ModElementReference public List<MItemBlock> compatibleItems;
	public boolean excludeItems;

	public boolean isTreasureEnchantment;
	public boolean isCurse;
	public boolean isAllowedOnBooks;
	public boolean canGenerateInLootTables;
	public boolean canVillagerTrade;

	private Enchantment() {
		this(null);
	}

	public Enchantment(ModElement element) {
		super(element);

		canGenerateInLootTables = true;
		canVillagerTrade = true;
	}

}
