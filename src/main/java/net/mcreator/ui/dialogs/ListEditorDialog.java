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

package net.mcreator.ui.dialogs;

import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.UniqueNameValidator;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class ListEditorDialog {

	/**
	 * Opens a dialog to edit string entries contained in the provided list.
	 *
	 * @param parent        The workspace window this method was called from.
	 * @param textList      List of string entries that are about to be edited.
	 * @param validator     Supplier of validators used on list entries' text fields, {@code null} means no validation.
	 * @param uniqueEntries If {@code true}, duplicate list entries will not be allowed.
	 * @return True if user chose OK option after editing strings list, false if it was cancel/close option.
	 */
	public static List<String> open(Window parent, List<String> textList,
			@Nullable Function<VTextField, Validator> validator, boolean uniqueEntries) {
		AtomicReference<List<String>> retVal = new AtomicReference<>();
		MCreatorDialog dialog = new MCreatorDialog(parent, L10N.t("dialog.list_editor.title"), true);

		List<ListEntry> entryList = new ArrayList<>();
		JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));
		for (String entry : textList)
			new ListEntry(entryList, entries, entry, validator, uniqueEntries);

		JButton add = new JButton(UIRES.get("16px.add.gif"));
		add.setText(L10N.t("dialog.list_editor.add"));
		add.addActionListener(e -> new ListEntry(entryList, entries, "", validator, uniqueEntries));

		JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.add("North", PanelUtils.join(new JLabel(), add, new JLabel()));
		listPanel.add("Center", new JScrollPane(PanelUtils.pullElementUp(entries)));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		dialog.getRootPane().setDefaultButton(ok);

		ok.addActionListener(e -> {
			if (new AggregatedValidationResult(
					entryList.stream().map(s -> s.valueField).toArray(IValidable[]::new)).validateIsErrorFree()) {
				retVal.set(new ArrayList<>());
				for (ListEntry entry : entryList)
					retVal.get().add(entry.valueField.getText());
				dialog.setVisible(false);
			} else {
				JOptionPane.showMessageDialog(parent, L10N.t("dialog.list_editor.errors.message"),
						L10N.t("dialog.list_editor.errors.title"), JOptionPane.ERROR_MESSAGE);
			}
		});
		cancel.addActionListener(e -> dialog.setVisible(false));

		dialog.getContentPane().add(PanelUtils.centerAndSouthElement(listPanel, PanelUtils.join(ok, cancel)));
		dialog.setSize(420, 350);
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);

		return retVal.get();
	}

	private static final class ListEntry extends JPanel {
		private final VTextField valueField = new VTextField(20);

		private ListEntry(List<ListEntry> entryList, JPanel parent, String value,
				@Nullable Function<VTextField, Validator> validator, boolean uniqueEntries) {
			super(new FlowLayout(FlowLayout.LEFT));
			setOpaque(false);

			Validator lev = validator == null ? null : validator.apply(valueField);
			if (uniqueEntries)
				lev = new UniqueNameValidator(L10N.t("dialog.list_editor.validator"), valueField::getText,
						() -> entryList.stream().map(e -> e.valueField.getText()), lev);

			valueField.setText(value);
			if (lev != null) {
				valueField.setValidator(lev);
				valueField.enableRealtimeValidation();
			}

			final JComponent container = PanelUtils.expandHorizontally(this);
			parent.add(container);
			entryList.add(this);

			JButton remove = new JButton(UIRES.get("16px.clear"));
			remove.setText(L10N.t("dialog.list_editor.remove"));
			remove.addActionListener(e -> {
				entryList.remove(this);
				parent.remove(container);
				parent.revalidate();
				parent.repaint();
			});

			add(valueField);
			add(remove);

			parent.revalidate();
			parent.repaint();
		}
	}
}
