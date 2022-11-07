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
import net.mcreator.element.parts.gui.Label;
import net.mcreator.element.parts.procedure.StringProcedure;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.procedure.StringProcedureSelector;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class LabelDialog extends AbstractWYSIWYGDialog<Label> {

	public LabelDialog(WYSIWYGEditor editor, @Nullable Label label) {
		super(editor.mcreator, label);
		setSize(560, 280);
		setLocationRelativeTo(editor.mcreator);
		setModal(true);

		StringProcedureSelector textSelector = new StringProcedureSelector(
				IHelpContext.NONE.withEntry("gui/label_text"), editor.mcreator, new JTextField(),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		textSelector.refreshList();

		ProcedureSelector displayCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/label_display_condition"), editor.mcreator,
				L10N.t("dialog.gui.label_event_display_condition"), ProcedureSelector.Side.CLIENT, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		displayCondition.refreshList();

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		add("North", PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.gui.label_text"), textSelector));

		add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.northAndCenterElement(options, PanelUtils.join(FlowLayout.LEFT, displayCondition), 20, 5)));

		setTitle(L10N.t("dialog.gui.label_component_title"));

		final JColor cola = new JColor(editor.mcreator, false, false);

		if (editor.renderBgLayer.isSelected()) {
			cola.setColor(new Color(60, 60, 60));
		} else {
			cola.setColor(Color.white);
		}

		options.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.gui.label_text_color"), cola));
		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));

		getRootPane().setDefaultButton(ok);

		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		if (label != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			textSelector.setSelectedProcedure(label.text);
			cola.setColor(label.color);
			displayCondition.setSelectedProcedure(label.displayCondition);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			setVisible(false);
			StringProcedure text = textSelector.getSelectedProcedure();

			String name = text.getFixedValue();
			if (text.getName() != null)
				name = text.getName() + " (string procedure)";

			if (label == null) {
				Label component = new Label(name, 0, 0, text, cola.getColor(), displayCondition.getSelectedProcedure());

				setEditingComponent(component);
				editor.editor.addComponent(component);
				editor.list.setSelectedValue(component, true);
				editor.editor.moveMode();
			} else {
				int idx = editor.components.indexOf(label);
				editor.components.remove(label);
				Label labelNew = new Label(name, label.getX(), label.getY(), text, cola.getColor(),
						displayCondition.getSelectedProcedure());
				editor.components.add(idx, labelNew);
				setEditingComponent(labelNew);
			}
		});

		setVisible(true);
	}

}
