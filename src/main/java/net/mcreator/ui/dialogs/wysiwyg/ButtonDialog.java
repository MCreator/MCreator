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
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.IMachineNamedComponent;
import net.mcreator.io.Transliteration;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.JavaMemberNameValidator;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class ButtonDialog extends AbstractWYSIWYGDialog {

	public ButtonDialog(WYSIWYGEditor editor, @Nullable Button button) {
		super(editor.mcreator, button);
		setModal(true);
		setSize(480, 260);
		setLocationRelativeTo(editor.mcreator);
		setTitle(L10N.t("dialog.gui.button_add_title"));

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		VTextField nameField = new VTextField(20);
		nameField.setPreferredSize(new Dimension(200, 28));
		nameField.enableRealtimeValidation();
		Validator validator = new JavaMemberNameValidator(nameField, false);
		nameField.setValidator(() -> {
			String textname = Transliteration.transliterateString(nameField.getText());
			for (int i = 0; i < editor.list.getModel().getSize(); i++) {
				GUIComponent component = editor.list.getModel().getElementAt(i);
				if (button != null && component.name.equals(button.name)) // skip current element if edit mode
					continue;
				if (component instanceof IMachineNamedComponent && component.name.equals(textname))
					return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
							L10N.t("common.name_already_exists"));
			}
			return validator.validate();
		});
		options.add(PanelUtils.join(L10N.label("dialog.gui.button_name"), nameField));

		JTextField fieldText = new JTextField(20);
		options.add(PanelUtils.join(L10N.label("dialog.gui.button_text"), fieldText));
		fieldText.setPreferredSize(new Dimension(200, 28));

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

		add("North", PanelUtils.northAndCenterElement(PanelUtils.centerInPanel(button == null ?
				L10N.label("dialog.gui.button_change_width") :
				L10N.label("dialog.gui.button_resize")), PanelUtils.centerInPanel(options)));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		getRootPane().setDefaultButton(ok);

		if (button != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			nameField.setText(button.name);
			eh.setSelectedProcedure(button.onClick);
			displayCondition.setSelectedProcedure(button.displayCondition);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			setVisible(false);
			String text = Transliteration.transliterateString(nameField.getText());
			if (!text.equals("")) {
				if (button == null) {
					int textwidth = (int) (WYSIWYG.fontMC.getStringBounds(fieldText.getText(), WYSIWYG.frc).getWidth());
					editor.editor.setPositioningMode(textwidth + 25, 20);
					editor.editor.setPositionDefinedListener(e -> editor.editor.addComponent(
							new Button(text, editor.editor.newlyAddedComponentPosX,
									editor.editor.newlyAddedComponentPosY, fieldText.getText(), editor.editor.ow,
									editor.editor.oh, eh.getSelectedProcedure(),
									displayCondition.getSelectedProcedure())));
				} else {
					int idx = editor.components.indexOf(button);
					editor.components.remove(button);
					Button buttonNew = new Button(text, button.getX(), button.getY(), fieldText.getText(), button.width,
							button.height, eh.getSelectedProcedure(), displayCondition.getSelectedProcedure());
					editor.components.add(idx, buttonNew);
					setEditingComponent(buttonNew);
				}
			}
		});

		setVisible(true);
	}

}
