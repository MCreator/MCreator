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

import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringSelectorDialog extends ListSelectorDialog<String> {
	public StringSelectorDialog(MCreator mcreator, Function<Workspace, String[]> entryProvider,
			List<String> initialSelection) {
		super(mcreator, entryProvider.andThen(Arrays::asList), initialSelection);
		list.setCellRenderer(new StringListCellRenderer());
	}

	@Override protected Predicate<String> getFilter(String term) {
		return s -> s.toLowerCase(Locale.ENGLISH).contains(term.toLowerCase(Locale.ENGLISH));
	}

	public static class Builder {
		private final MCreator mcreator;
		private final Function<Workspace, String[]> entryProvider;
		private final String message;
		private final String title;

		private List<String> initialSelection = null;

		public Builder(MCreator mcreator, Function<Workspace, String[]> entryProvider,
				String message) {
			this(mcreator, entryProvider, L10N.t("dialog.selector.title"), message);
		}

		public Builder(MCreator mcreator, Function<Workspace, String[]> entryProvider,
				String title, String message) {
			this.mcreator = mcreator;
			this.entryProvider = entryProvider;
			this.title = title;
			this.message = message;
		}

		public Builder initialSelection(String entry) {
			return this.initialSelection(entry == null ? null : List.of(entry));
		}

		public Builder initialSelection(List<String> initialSelection) {
			this.initialSelection = initialSelection;
			return this;
		}

		public String open() {
			var stringListSelector = new StringSelectorDialog(mcreator, entryProvider, initialSelection);
			stringListSelector.setTitle(title);
			stringListSelector.setMessage(message);
			stringListSelector.setVisible(true);

			return stringListSelector.list.getSelectedValue();
		}

		public List<String> openMulti() {
			var stringListSelector = new StringSelectorDialog(mcreator, entryProvider, initialSelection);
			stringListSelector.list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			stringListSelector.setTitle(title);
			stringListSelector.setMessage(message);
			stringListSelector.setVisible(true);

			return stringListSelector.list.getSelectedValuesList();
		}

	}

	private class StringListCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			var label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setText(value.toString().replace("CUSTOM:", ""));
			if (value.toString().contains("CUSTOM:")) {
				String[] parts = value.toString().split(":");
				setIcon(new ImageIcon(ImageUtils.resize(
						MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), parts[0] + ":" + parts[1]).getImage(),
						18)));
			}
			return label;
		}
	}
}