/*
 * MCToolkit (https://mctoolkit.net/)
 * Copyright (C) 2020 MCToolkit and contributors
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
import net.mcreator.element.parts.EffectEntry;
import net.mcreator.workspace.elements.ModElement;

import java.util.ArrayList;
import java.util.List;

public class PotionItem extends GeneratableElement {
	public String potionName;
	public String splashName;
	public String lingeringName;
	public String arrowName;
	public List<CustomEffectEntry> effects;

	public PotionItem(ModElement element) {
		super(element);
		effects = new ArrayList<>();
	}

	public static class CustomEffectEntry {

		public EffectEntry effect;
		public int duration;
		public int amplifier;

	}
}
