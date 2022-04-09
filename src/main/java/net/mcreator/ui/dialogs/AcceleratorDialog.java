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

import net.mcreator.element.ModElementType;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.action.accelerators.AcceleratorsManager;
import net.mcreator.ui.action.accelerators.JAcceleratorButton;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.tools.MaterialPackMakerTool;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.MCreatorTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class AcceleratorDialog extends MCreatorDialog {

	public AcceleratorDialog(MCreator mcreator) {
		super(mcreator, L10N.t("dialog.accelerators.title"), true);

		List<JAcceleratorButton> buttonsList = new ArrayList<>();

		JPanel northPanel = new JPanel(new BorderLayout(20, 20));
		northPanel.add(L10N.label("dialog.accelerators.description"), "North");

		JComboBox<String> sections = new JComboBox<>(
				AcceleratorsManager.INSTANCE.SECTIONS.stream().map(s -> L10N.t("dialog.accelerators.section." + s))
						.toArray(String[]::new));

		// Just an Easter egg because it's nice
		final String[] str = { "" };
		sections.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_M)
					str[0] = "m";
				if (str[0].equals("m") && e.getKeyCode() == KeyEvent.VK_C)
					str[0] += "c";
				if (str[0].equals("mc") && e.getKeyCode() == KeyEvent.VK_R) {
					GeneratorConfiguration gc = mcreator.getGeneratorConfiguration();
					if (gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
							!= GeneratorStats.CoverageStatus.NONE
							&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.ITEM)
							!= GeneratorStats.CoverageStatus.NONE
							&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.BLOCK)
							!= GeneratorStats.CoverageStatus.NONE
							&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.TOOL)
							!= GeneratorStats.CoverageStatus.NONE
							&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.ARMOR)
							!= GeneratorStats.CoverageStatus.NONE) {
						MaterialPackMakerTool.addMaterialPackToWorkspace(mcreator, mcreator.getWorkspace(), "MCreator",
								"Gem based", MCreatorTheme.MAIN_TINT_DEFAULT, 4.84);
						mcreator.mv.updateMods();
					}
				}
			}
		}); // End of the Easter egg

		northPanel.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.accelerators.select_section"), sections),
				"Center");

		List<BasicAction> actions = mcreator.actionRegistry.getActions().stream()
				.filter(a -> a instanceof BasicAction ba && ba.getAccelerator() != null).map(a -> (BasicAction) a)
				.toList();

		CardLayout cl = new CardLayout();
		JPanel sectionsPanel = new JPanel(cl);
		sectionsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("dialog.accelerators.accelerators"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		sections.addActionListener(e -> cl.show(sectionsPanel, (String) sections.getSelectedItem()));

		AcceleratorsManager.INSTANCE.SECTIONS.forEach(sectionName -> {
			List<JComponent> comps = new ArrayList<>();
			actions.stream().filter(action -> action.getAccelerator().getSection().equals(sectionName))
					.forEach(action -> {
						KeyStroke keyStroke = action.getAccelerator().getKeyStroke();
						if (keyStroke != null) {
							comps.add(new JLabel(action.getName()));
							JAcceleratorButton button = new JAcceleratorButton(setButtonText(keyStroke),
									action.getAccelerator());
							comps.add(button);
							buttonsList.add(button);
						}
					});

			sectionsPanel.add(new JScrollPane(PanelUtils.pullElementUp(
							PanelUtils.gridElements(comps.size() / 2, 2, 5, 5, comps.toArray(new JComponent[0])))),
					L10N.t("dialog.accelerators.section." + sectionName));
		});

		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		JButton confirm = new JButton(L10N.t("common.confirmation"));
		confirm.addActionListener(a -> {
			AcceleratorsManager.INSTANCE.loadAccelerators(mcreator.actionRegistry);
			AcceleratorDialog.this.setVisible(false);
		});
		buttonsPanel.add(confirm);

		JButton resetAll = new JButton(L10N.t("dialog.accelerators.reset_all"));
		resetAll.addActionListener(a -> {
			int n = JOptionPane.showConfirmDialog(mcreator.actionRegistry.getMCreator(),
					L10N.t("dialog.accelerators.reset_all.message"), L10N.t("common.confirmation"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (n == JOptionPane.YES_OPTION) {
				AcceleratorsManager.INSTANCE.setDefaultValues(mcreator.actionRegistry);
				// We open a new dialog, so we don't need to iterate every button to change their value
				setVisible(false);
				new AcceleratorDialog(mcreator);
			}
		});
		buttonsPanel.add(resetAll);

		JButton cancel = new JButton(L10N.t("common.close"));
		cancel.addActionListener(a -> {
			setVisible(false);
			buttonsList.forEach(button -> AcceleratorsManager.INSTANCE.setInCache(button.getAcceleratorID(),
					button.getKeyStroke()));
		});
		buttonsPanel.add(cancel);

		add(PanelUtils.northAndCenterElement(PanelUtils.northAndCenterElement(new JEmptyBox(), northPanel, 5, 5),
				PanelUtils.centerAndSouthElement(sectionsPanel, buttonsPanel, 20, 20), 20, 20));

		setSize(700, 750);
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
		} else if (keyStroke.getKeyCode() == KeyEvent.VK_UNDEFINED) {
			return L10N.t("dialog.accelerators.none");
		}
		return acceleratorText + KeyEvent.getKeyText(keyStroke.getKeyCode());
	}

}
