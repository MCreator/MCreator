/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.action.accelerators.AcceleratorsManager;
import net.mcreator.ui.action.accelerators.JActionButton;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class AcceleratorDialog extends MCreatorDialog {

	public AcceleratorDialog(MCreator mcreator) {
		super(mcreator, L10N.t("dialog.accelerators.title"), true);

		ActionRegistry actionRegistry = mcreator.actionRegistry;

		JPanel panel = new JPanel(new BorderLayout(20, 20));
		panel.setOpaque(false);

		List<BasicAction> actions = actionRegistry.getActions().stream()
				.filter(a -> a instanceof BasicAction ba && ba.getAccelerator() != null).map(a -> (BasicAction) a)
				.toList();

		JPanel accelerators = new JPanel(new GridLayout(actions.size(), 2, 5, 5));

		actions.forEach(action -> {
			KeyStroke keyStroke = action.getAccelerator().getKeyStroke();
			if (keyStroke != null) {
				accelerators.add(new JLabel(action.getName()));
				JActionButton button = new JActionButton(setButtonText(keyStroke), action);
				accelerators.add(button);
			}
		});

		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonsPanel.setOpaque(false);

		JButton confirm = new JButton(L10N.t("common.confirmation"));
		confirm.addActionListener(a -> {
			AcceleratorsManager.INSTANCE.loadAccelerators(actionRegistry, true);
			AcceleratorDialog.this.setVisible(false);
		});
		buttonsPanel.add(confirm);

		JButton resetAll = new JButton(L10N.t("dialog.accelerators.reset_all"));
		resetAll.addActionListener(a -> {
			AcceleratorsManager.INSTANCE.resetAll(actionRegistry);
			// We open a new dialog, so we don't need to change all buttons.
			setVisible(false);
			new AcceleratorDialog(mcreator);
		});
		buttonsPanel.add(resetAll);

		JButton cancel = new JButton(L10N.t("common.close"));
		cancel.addActionListener(a -> {
			setVisible(false);
			for (Component component : accelerators.getComponents()) {
				if (component instanceof JActionButton button)
					AcceleratorsManager.INSTANCE.setInCache(button.getAcceleratorID(), button.getKeyStroke());
			}
		});
		buttonsPanel.add(cancel);

		panel.add(L10N.label("dialog.accelerators.description"), "North");

		JScrollPane scrollPane = new JScrollPane(accelerators);
		scrollPane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("dialog.accelerators.accelerators"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		panel.add(scrollPane, "Center");

		panel.add(buttonsPanel, "South");

		add(panel);

		setSize(600, 650);
		setResizable(true);
		setLocationRelativeTo(mcreator);
		setVisible(true);
	}

	public static String setButtonText(KeyStroke keyStroke) {
		String acceleratorText = "";
		int modifiers = keyStroke.getModifiers();
		if (modifiers > 0) {
			acceleratorText = InputEvent.getModifiersExText(modifiers);
			acceleratorText += " + ";
		}
		return acceleratorText + KeyEvent.getKeyText(keyStroke.getKeyCode());
	}

}
