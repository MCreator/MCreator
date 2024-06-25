/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.workspace.misc;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.workspace.elements.ModElement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CreativeTabsOrder extends ConcurrentHashMap<String, ArrayList<String>> {

	public void addOrUpdateModElementTabs(GeneratableElement element) {
		if (element instanceof ITabContainedElement tabElement) {
			List<TabEntry> tabEntries = tabElement.getCreativeTabs();
			String meName = element.getModElement().getName();

			// remove element from its prior tabs
			for (Entry<String, ArrayList<String>> entry : this.entrySet()) {
				boolean found = false;
				for (TabEntry tabEntry : tabEntries) {
					if (entry.getKey().equals(tabEntry.getUnmappedValue())) {
						found = true;
						break;
					}
				}
				if (!found)
					entry.getValue().remove(meName);
			}

			for (TabEntry tabEntry : tabEntries) {
				ArrayList<String> tabContents = this.get(tabEntry.getUnmappedValue());

				// if order in new tab is overridden, add the element explicitly
				if (tabContents != null && !tabContents.contains(meName))
					tabContents.add(meName);
			}
		}
	}

	public void removeModElementFromTabs(GeneratableElement element) {
		if (element instanceof ITabContainedElement) {
			for (ArrayList<String> tabContents : this.values())
				tabContents.remove(element.getModElement().getName());
		}
	}

	public void setElementOrderInTab(String tab, List<ModElement> elements) {
		if (this.containsKey(tab))
			this.get(tab).clear();
		else
			this.put(tab, new ArrayList<>());

		for (ModElement element : elements)
			this.get(tab).add(element.getName());
	}

}
