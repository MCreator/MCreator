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
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <p>A general filter model that is used inside resources-related workspace panels.
 * It defines how a general resource panel's list of elements behaves.</p>
 *
 * @param <T>
 */
public class ResourceFilterModel<T> extends DefaultListModel<T> {
	private final List<T> items;
	private final List<T> filterItems;
	private final WorkspacePanel workspacePanel;
	private final BiFunction<T, String, Boolean> refilterItemsFilter;
	private final Function<T, String> resourceNameSupplier;

	public ResourceFilterModel(WorkspacePanel workspacePanel, Function<T, String> resourceNameSupplier) {
		this(workspacePanel,
				(item, query) -> resourceNameSupplier.apply(item).toLowerCase(Locale.ENGLISH).contains(query),
				Object::toString);
	}

	/**
	 * @param workspacePanel       <p>The {@link WorkspacePanel} of the current workspace</p>
	 * @param refilterItemsFilter  <p>Defines which elements should be contained inside the filtered list of elements.</p>
	 * @param resourceNameSupplier <p>Provides resource name used for sort-by-name sorter.</p>
	 */
	public ResourceFilterModel(WorkspacePanel workspacePanel, BiFunction<T, String, Boolean> refilterItemsFilter,
			Function<T, String> resourceNameSupplier) {
		this.workspacePanel = workspacePanel;
		this.refilterItemsFilter = refilterItemsFilter;
		this.resourceNameSupplier = resourceNameSupplier;

		items = new ArrayList<>();
		filterItems = new ArrayList<>();
	}

	@Override public void addAll(Collection<? extends T> collection) {
		items.addAll(collection);
		refilter();
	}

	@SuppressWarnings("SuspiciousMethodCalls") @Override public int indexOf(Object elem) {
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

	@SuppressWarnings("SuspiciousMethodCalls") @Override public boolean removeElement(Object a) {
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
		filterItems.addAll(items.stream().filter(Objects::nonNull).filter(item -> refilterItemsFilter.apply(item,
				workspacePanel.search.getText().toLowerCase(Locale.ENGLISH))).toList());

		if (workspacePanel.sortName.isSelected())
			filterItems.sort(Comparator.comparing(resourceNameSupplier));

		if (workspacePanel.desc.isSelected())
			Collections.reverse(filterItems);

		fireContentsChanged(this, 0, getSize());
	}

}
