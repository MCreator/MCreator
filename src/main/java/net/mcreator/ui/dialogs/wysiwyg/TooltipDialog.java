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

package net.mcreator.ui.dialogs.wysiwyg;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.gui.Tooltip;
import net.mcreator.element.parts.procedure.StringProcedure;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.procedure.StringProcedureSelector;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TooltipDialog extends AbstractWYSIWYGDialog<Tooltip> {

	public TooltipDialog(WYSIWYGEditor editor, @Nullable Tooltip tooltip) {
		super(editor, tooltip);
		setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		setSize(480, 195);
		setLocationRelativeTo(editor.mcreator);

		JTextField textField = new JTextField();

		addWindowListener(new WindowAdapter() {
			@Override public void windowActivated(WindowEvent e) {
				SwingUtilities.invokeLater(textField::requestFocus);
			}
		});

		StringProcedureSelector labelText = new StringProcedureSelector(IHelpContext.NONE.withEntry("gui/tooltip_text"),
				editor.mcreator, textField, 100,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		labelText.refreshList();

		ProcedureSelector displayCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/tooltip_display_condition"), editor.mcreator,
				L10N.t("dialog.gui.tooltip_display_condition"), ProcedureSelector.Side.CLIENT, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		displayCondition.refreshList();

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		add("North", PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.gui.label_text"), labelText));

		add("Center", PanelUtils.centerInPanel(displayCondition));

		setTitle(L10N.t("dialog.gui.add_tooltip"));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));

		getRootPane().setDefaultButton(ok);

		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		if (tooltip != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			labelText.setSelectedProcedure(tooltip.text);
			displayCondition.setSelectedProcedure(tooltip.displayCondition);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			setVisible(false);
			StringProcedure textProcedure = labelText.getSelectedProcedure();

			if (tooltip == null) {
				String nameBase;
				if (textProcedure.getName() != null) { // string procedure
					nameBase = "proc_" + RegistryNameFixer.fromCamelCase(textProcedure.getName());
				} else { // fixed text
					nameBase = textProcedure.getFixedValue();
				}

				String name = textToMachineName(editor.getComponentList(), "tooltip_", nameBase);

				int textwidth = (int) (WYSIWYG.fontMC.getStringBounds(textProcedure.getName() == null ?
						textProcedure.getFixedValue() : textProcedure.getName(), WYSIWYG.frc).getWidth());

				int textheight = (int) (WYSIWYG.fontMC.getStringBounds(textProcedure.getName() == null ?
						textProcedure.getFixedValue() : textProcedure.getName(), WYSIWYG.frc).getHeight());

				Tooltip component = new Tooltip(name, 0, 0, textwidth, textheight, textProcedure,
						displayCondition.getSelectedProcedure());

				setEditingComponent(component);
				editor.editor.addComponent(component);
				editor.list.setSelectedValue(component, true);
				editor.editor.moveMode();
			} else {
				int idx = editor.components.indexOf(tooltip);
				editor.components.remove(tooltip);
				Tooltip tooltipNew = new Tooltip(tooltip.name, tooltip.getX(), tooltip.getY(), tooltip.getWidth(editor.mcreator.getWorkspace()),
						tooltip.getHeight(editor.mcreator.getWorkspace()), textProcedure, displayCondition.getSelectedProcedure());
				editor.components.add(idx, tooltipNew);
				setEditingComponent(tooltipNew);
			}
		});

		setVisible(true);
	}
}
