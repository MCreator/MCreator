/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.ui.dialogs;

import net.mcreator.ui.MCreator;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This abstract class provides a filter field and a filter model for searchable selectors.
 *
 * @param <T> The type of the elements in this selector dialog
 */
public abstract class SearchableSelectorDialog<T> extends MCreatorDialog {
	final MCreator mcreator;
	final Function<Workspace, List<T>> provider;

	final FilterModel model = new FilterModel();
	final JTextField filterField = new JTextField(14);

	public SearchableSelectorDialog(MCreator mcreator, Function<Workspace, List<T>> provider) {
		super(mcreator);
		this.mcreator = mcreator;
		this.provider = provider;

		setModalityType(ModalityType.APPLICATION_MODAL);

		filterField.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void removeUpdate(DocumentEvent arg0) {
				model.refilter();
			}

			@Override public void insertUpdate(DocumentEvent arg0) {
				model.refilter();
			}

			@Override public void changedUpdate(DocumentEvent arg0) {
				model.refilter();
			}
		});
	}

	class FilterModel extends DefaultListModel<T> {
		ArrayList<T> entries;
		ArrayList<T> filterEntries;

		FilterModel() {
			super();
			entries = new ArrayList<>();
			filterEntries = new ArrayList<>();
		}

		@Override public T getElementAt(int index) {
			if (index < filterEntries.size())
				return filterEntries.get(index);
			else
				return null;
		}

		@Override public int getSize() {
			return filterEntries.size();
		}

		@Override public void addElement(T o) {
			entries.add(o);
			refilter();
		}

		@Override public void removeAllElements() {
			super.removeAllElements();
			entries.clear();
			filterEntries.clear();
		}

		@Override public boolean removeElement(Object a) {
			if (a != null) {
				entries.remove(a);
				filterEntries.remove(a);
			}
			return super.removeElement(a);
		}

		void refilter() {
			filterEntries.clear();
			String term = filterField.getText();
			filterEntries.addAll(entries.stream().filter(getFilter(term)).toList());
			fireContentsChanged(this, 0, getSize());
		}
	}

	/**
	 * The filter used by the search bar
	 *
	 * @param term The text contained in the search bar
	 * @return The predicate that will be used to filter the elements
	 */
	abstract Predicate<T> getFilter(String term);

	@Override public void setVisible(boolean visible) {
		if (visible) {
			reloadElements();
		}
		super.setVisible(visible);
	}

	void reloadElements() {
		model.removeAllElements();
		provider.apply(this.mcreator.getWorkspace()).forEach(model::addElement);
	}
}
