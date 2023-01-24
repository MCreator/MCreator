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

import net.mcreator.element.NamespacedGeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.interfaces.IOtherModElementsDependent;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.workspace.elements.ModElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused") public class LootTable extends NamespacedGeneratableElement
		implements IOtherModElementsDependent {

	public String type;

	public List<Pool> pools;

	public LootTable(ModElement element) {
		super(element);
	}

	@Override public Collection<? extends MappableElement> getUsedElementMappings() {
		Collection<MappableElement> entries = new ArrayList<>();
		for (Pool pool : pools) {
			for (Pool.Entry entry : pool.entries)
				entries.add(entry.item);
		}
		return entries;
	}

	public static class Pool {
		public int minrolls, maxrolls;
		public int minbonusrolls, maxbonusrolls;
		public boolean hasbonusrolls;

		public List<Entry> entries;

		public static class Entry {

			public String type;
			public MItemBlock item;

			public int weight;

			public int minCount, maxCount;

			public int minEnchantmentLevel, maxEnchantmentLevel;

			public boolean affectedByFortune, explosionDecay;

			public int silkTouchMode;

			// initialize default values
			public Entry() {
				this.weight = 1;
				this.minCount = 1;
				this.maxCount = 1;
			}

		}

	}
}