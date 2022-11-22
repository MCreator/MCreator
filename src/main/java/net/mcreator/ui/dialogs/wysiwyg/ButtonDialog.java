/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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
import net.mcreator.element.parts.gui.Button;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;

public class ButtonDialog extends AbstractWYSIWYGDialog<Button> {

	public ButtonDialog(WYSIWYGEditor editor, @Nullable Button button) {
		super(editor, button);
		setModal(true);
		setSize(480, 220);
		setLocationRelativeTo(editor.mcreator);
		setTitle(L10N.t("dialog.gui.button_add_title"));
		JTextField buttonText = new JTextField(20);
		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		options.add(PanelUtils.join(L10N.label("dialog.gui.button_text"), buttonText));

		ProcedureSelector eh = new ProcedureSelector(IHelpContext.NONE.withEntry("gui/on_button_clicked"),
				editor.mcreator, L10N.t("dialog.gui.button_event_on_clicked"), ProcedureSelector.Side.BOTH, false,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		eh.refreshList();

		ProcedureSelector displayCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/button_display_condition"), editor.mcreator,
				L10N.t("dialog.gui.button_display_condition"), ProcedureSelector.Side.BOTH, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		displayCondition.refreshList();

		add("Center",
				new JScrollPane(PanelUtils.centerInPanel(PanelUtils.gridElements(1, 2, 5, 5, eh, displayCondition))));

		add("North", PanelUtils.northAndCenterElement(button == null ?
				PanelUtils.centerInPanel(L10N.label("dialog.gui.button_change_width")) :
				PanelUtils.centerInPanel(L10N.label("dialog.gui.button_resize")), PanelUtils.centerInPanel(options)));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		getRootPane().setDefaultButton(ok);

		if (button != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			buttonText.setText(button.name);
			eh.setSelectedProcedure(button.onClick);
			displayCondition.setSelectedProcedure(button.displayCondition);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			setVisible(false);
			String text = buttonText.getText();
			if (button == null) {
				String name = textToMachineName(editor.getComponentList(), "button_", text);

				int textwidth = (int) (WYSIWYG.fontMC.getStringBounds(text, WYSIWYG.frc).getWidth());

				Button component = new Button(name, 0, 0, text, textwidth + 25, 20,
						eh.getSelectedProcedure(), displayCondition.getSelectedProcedure());

				setEditingComponent(component);
				editor.editor.addComponent(component);
				editor.list.setSelectedValue(component, true);
				editor.editor.moveMode();
			} else {
				int idx = editor.components.indexOf(button);
				editor.components.remove(button);
				Button buttonNew = new Button(button.name, button.getX(), button.getY(), text, button.width, button.height,
						eh.getSelectedProcedure(), displayCondition.getSelectedProcedure());
				editor.components.add(idx, buttonNew);
				setEditingComponent(buttonNew);
			}
		});

		setVisible(true);
	}

}
