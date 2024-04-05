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

package net.mcreator.workspace;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.workspace.elements.ModElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TabUtils {

	public static List<String> getElementOrderInTab(Workspace workspace, String tab) {
		return workspace.getTabElementOrderMap().get(tab);
	}

	public static void setElementOrderInTab(Workspace workspace, String tab, List<ModElement> elements) {
		if (workspace.getTabElementOrderMap().containsKey(tab))
			workspace.getTabElementOrderMap().get(tab).clear();
		else
			workspace.getTabElementOrderMap().put(tab, new ArrayList<>());

		for (ModElement element : elements)
			workspace.getTabElementOrderMap().get(tab).add(element.getName());

		workspace.markDirty();
	}

	public static void addModElementToTabs(Workspace workspace, ModElement element) {
		if (element.getGeneratableElement() instanceof ITabContainedElement tabElement) {
			TabEntry tabEntry = tabElement.getCreativeTab();
			if (tabEntry != null && !(tabEntry.getUnmappedValue()).equals("No creative tab entry")
					&& workspace.getTabElementOrderMap().containsKey(tabEntry.getUnmappedValue()))
				workspace.getTabElementOrderMap().get(tabEntry.getUnmappedValue()).add(element.getName());
		}
	}

	public static void updateModElementTabs(Workspace workspace, GeneratableElement element) {
		if (element instanceof ITabContainedElement tabElement) {
			TabEntry tabEntry = tabElement.getCreativeTab();
			if (tabEntry == null || tabEntry.getUnmappedValue().equals("No creative tab entry"))
				return;

			// if order in new tab is overridden, add the element explicitly
			String meName = element.getModElement().getName();
			if (workspace.getTabElementOrderMap().containsKey(tabEntry.getUnmappedValue())
					&& !workspace.getTabElementOrderMap().get(tabEntry.getUnmappedValue()).contains(meName)) {
				for (Map.Entry<String, ArrayList<String>> entry : workspace.getTabElementOrderMap().entrySet()) {
					if (!entry.getKey().equals(tabEntry.getUnmappedValue())) // remove element from its prior tab
						entry.getValue().remove(meName);
				}

				workspace.getTabElementOrderMap().get(tabEntry.getUnmappedValue()).add(meName);
			}
		}
	}

	public static void removeModElementFromTabs(Workspace workspace, ModElement element) {
		if (element.getGeneratableElement() instanceof ITabContainedElement) {
			for (ArrayList<String> tabContents : workspace.getTabElementOrderMap().values())
				tabContents.remove(element.getName());
		}
	}

}
