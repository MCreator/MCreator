/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.workspace.resources;

import net.mcreator.ui.workspace.WorkspacePanel;

import javax.swing.*;
import java.util.*;
import java.util.function.Predicate;

public class ResourceFilterModel<T> extends DefaultListModel<T> {
	private final List<T> items;
	private final List<T> filterItems;
	private final WorkspacePanel workspacePanel;
	private final Predicate<T> refilterItemsFilter;
	private final Comparator<T> sortingCondition;

	public ResourceFilterModel(WorkspacePanel workspacePanel, Predicate<T> refilterItemsFilter,
			Comparator<T> sortingCondition) {
		super();
		this.workspacePanel = workspacePanel;
		this.refilterItemsFilter = refilterItemsFilter;
		this.sortingCondition = sortingCondition;

		items = new ArrayList<>();
		filterItems = new ArrayList<>();
	}

	@Override public int indexOf(Object elem) {
		try {
			return filterItems.indexOf(elem);
		} catch (ClassCastException e) {
			return -1;
		}
	}

	@Override public T getElementAt(int index) {
		if (index < filterItems.size())
			return filterItems.get(index);
		else
			return null;
	}

	@Override public int getSize() {
		return filterItems.size();
	}

	@Override public void addElement(T o) {
		items.add(o);
		refilter();
	}

	@Override public void removeAllElements() {
		super.removeAllElements();
		items.clear();
		filterItems.clear();
	}

	@Override public boolean removeElement(Object a) {
		if (a != null) {
			try {
				items.remove(a);
				filterItems.remove(a);
			} catch (ClassCastException ignored) {
			}
		}
		return super.removeElement(a);
	}

	void refilter() {
		filterItems.clear();
		filterItems.addAll(items.stream().filter(Objects::nonNull).filter(refilterItemsFilter).toList());

		if (workspacePanel.sortName.isSelected())
			filterItems.sort(sortingCondition);

		if (workspacePanel.desc.isSelected())
			Collections.reverse(filterItems);

		fireContentsChanged(this, 0, getSize());
	}
}
