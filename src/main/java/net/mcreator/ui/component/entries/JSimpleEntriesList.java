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

package net.mcreator.ui.component.entries;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.help.IHelpContext;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public abstract class JSimpleEntriesList<T extends JSimpleListEntry<U>, U> extends JSingleEntriesList<T, U> {

	public JSimpleEntriesList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, gui);

		add.addActionListener(e -> {
			@SuppressWarnings("unchecked") T entry = (T) newEntry(entries, entryList);
			entry.reloadDataLists();
			entryList.add(entry);
			entry.setEnabled(this.isEnabled());
			registerEntryUI(entry);
			entryAddedByUserHandler();
		});
	}

	public void entryAddedByUserHandler() {
	}

	protected abstract JSimpleListEntry<U> newEntry(JPanel parent, List<T> entryList);

	@Override public final List<U> getEntries() {
		return entryList.stream().map(T::getEntry).filter(Objects::nonNull).toList();
	}

	@Override public final void setEntries(List<U> newEntries) {
		entryList.clear();
		entries.removeAll();
		newEntries.forEach(e -> {
			@SuppressWarnings("unchecked") T entry = (T) newEntry(entries, entryList);
			entry.reloadDataLists();
			entryList.add(entry);
			entry.setEnabled(isEnabled());
			registerEntryUI(entry);
			entry.setEntry(e);
		});
	}

	@Override public void reloadDataLists() {
		entryList.forEach(JSimpleListEntry::reloadDataLists);
	}

}
