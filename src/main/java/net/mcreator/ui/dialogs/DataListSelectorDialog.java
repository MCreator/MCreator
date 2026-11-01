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

import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.BlockItemIcons;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

public class DataListSelectorDialog extends ListSelectorDialog<DataListEntry> {
	public DataListSelectorDialog(MCreator mcreator, Function<Workspace, Collection<DataListEntry>> entryProvider,
			List<DataListEntry> initialSelection) {
		super(mcreator,
				workspace -> entryProvider.apply(workspace).stream().filter(e -> e.isSupportedInWorkspace(workspace))
						.toList(), initialSelection);
		list.setCellRenderer(new DataListCellRenderer());
	}

	@Override protected Predicate<DataListEntry> getFilter(String term) {
		return e -> e.getReadableName().toLowerCase(Locale.ENGLISH).contains(term.toLowerCase(Locale.ENGLISH));
	}

	public static class Builder {
		private final MCreator mcreator;
		private final Function<Workspace, Collection<DataListEntry>> entryProvider;
		private final String message;
		private final String title;

		private List<DataListEntry> initialSelection = null;

		public Builder(MCreator mcreator, Function<Workspace, Collection<DataListEntry>> entryProvider,
				String message) {
			this(mcreator, entryProvider, L10N.t("dialog.selector.title"), message);
		}

		public Builder(MCreator mcreator, Function<Workspace, Collection<DataListEntry>> entryProvider,
				String title, String message) {
			this.mcreator = mcreator;
			this.entryProvider = entryProvider;
			this.title = title;
			this.message = message;
		}

		public Builder initialSelection(MappableElement entry) {
			return this.initialSelection(entry == null ? null : entry.getUnmappedValue());
		}

		public Builder initialSelection(String entry) {
			return this.initialSelection(entry == null ? null : new DataListEntry.Dummy(entry));
		}

		public Builder initialSelection(DataListEntry entry) {
			return this.initialSelection(entry == null ? null : List.of(entry));
		}

		public Builder initialSelection(List<DataListEntry> initialSelection) {
			this.initialSelection = initialSelection;
			return this;
		}

		public DataListEntry open() {
			var dataListSelector = new DataListSelectorDialog(mcreator, entryProvider, initialSelection);
			dataListSelector.setTitle(title);
			dataListSelector.setMessage(message);
			dataListSelector.setVisible(true);

			return dataListSelector.list.getSelectedValue();
		}

		public List<DataListEntry> openMulti() {
			var dataListSelector = new DataListSelectorDialog(mcreator, entryProvider, initialSelection);
			dataListSelector.list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			dataListSelector.setTitle(title);
			dataListSelector.setMessage(message);
			dataListSelector.setVisible(true);

			return dataListSelector.list.getSelectedValuesList();
		}
	}

	private static class DataListCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			var label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			DataListEntry entry = (DataListEntry) value;
			label.setText(entry.getReadableName());
			if (value instanceof DataListEntry.Custom custom) {
				setIcon(new ImageIcon(ImageUtils.resizeAA(
						MCItem.getBlockIconBasedOnName(custom.getModElement().getWorkspace(), custom.getName())
								.getImage(), 18)));
			} else if (entry.getTexture() != null) {
				setIcon(new ImageIcon(
						ImageUtils.resizeAA(BlockItemIcons.getIconForItem(entry.getTexture()).getImage(), 18)));
			}
			return label;
		}
	}

}