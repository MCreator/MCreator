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
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class StateEditorDialog {

	public static String open(MCreator mcreator, String initialState) {
		AtomicReference<String> retVal = new AtomicReference<>(initialState);
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.states.title"), true);
		dialog.getContentPane().setLayout(new BorderLayout());

		List<StateEntry> entryList = new ArrayList<>();
		AtomicInteger id = new AtomicInteger(0);
		JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));
		entries.setOpaque(false);

		if (initialState != null && !initialState.equals("")) {
			Arrays.asList(initialState.split(","))
					.forEach(e -> new StateEntry(entries, entryList).setEntry(e.split("=")[0], e.split("=")[1]));
		}

		JButton add = new JButton(UIRES.get("16px.add.gif"));
		add.setText(L10N.t("dialog.states.add"));
		add.addActionListener(e -> {
			id.set(Math.max(entryList.size(), id.get()) + 1);
			new StateEntry(entries, entryList).setEntry("property" + id.get(), "null");
		});

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		topbar.add(add);

		JPanel stateList = new JPanel(new BorderLayout());
		stateList.setOpaque(false);
		stateList.setPreferredSize(new Dimension(270, 340));
		stateList.add("North", topbar);
		stateList.add("Center", new JScrollPane(PanelUtils.pullElementUp(entries)));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		dialog.getRootPane().setDefaultButton(ok);

		ok.addActionListener(e -> {
			if (entryList.size() == 0) {
				retVal.set("");
				dialog.setVisible(false);
			} else if (entryList.stream().noneMatch(el -> el.entryKey.getValidationStatus().getValidationResultType()
					== Validator.ValidationResultType.ERROR)) {
				retVal.set(entryList.stream().map(el -> el.entryKey.getText() + "=" + el.entryValue.getText())
						.collect(Collectors.joining(",")));
				dialog.setVisible(false);
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		});
		cancel.addActionListener(e -> dialog.setVisible(false));

		dialog.getContentPane().add(PanelUtils.totalCenterInPanel(
						PanelUtils.centerAndSouthElement(new JScrollPane(stateList), PanelUtils.join(ok, cancel))),
				BorderLayout.CENTER);
		dialog.setSize(300, 400);
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);

		return retVal.get();
	}

	private static class StateEntry extends JPanel {

		private final VTextField entryKey = new VTextField(7); //TODO: Replace with property selector combo box
		private final JTextField entryValue = new JTextField(7); //TODO: Use logic from PreferencesDialog

		private StateEntry(JPanel parent, List<StateEntry> entryList) {
			super(new FlowLayout(FlowLayout.LEFT));

			final JComponent container = PanelUtils.expandHorizontally(this);

			parent.add(container);
			entryList.add(this);

			entryKey.setValidator(new RegistryNameValidator(entryKey, "Property name"));
			entryKey.enableRealtimeValidation();

			add(entryKey);
			add(new JLabel("="));
			add(entryValue);

			JButton remove = new JButton(UIRES.get("16px.clear"));
			remove.setToolTipText(L10N.t("elementgui.potion.remove_entry"));
			remove.addActionListener(e -> {
				entryList.remove(this);
				parent.remove(container);
				parent.revalidate();
				parent.repaint();
			});
			add(remove);

			parent.revalidate();
			parent.repaint();
		}

		public void setEntry(String keyText, String valueText) {
			entryKey.setText(keyText);
			entryValue.setText(valueText);
		}
	}

}

