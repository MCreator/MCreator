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
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.ProcedureSelector;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableElementType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class LabelDialog extends MCreatorDialog {

	public LabelDialog(WYSIWYGEditor editor, @Nullable Label label) {
		super(editor.mcreator);
		setSize(560, 180);
		setLocationRelativeTo(editor.mcreator);
		setModal(true);
		JComboBox<String> name = new JComboBox<>(new String[] { "Label text", "Text is <TextFieldName:text>",
				"This block is located at <x> <y> and <z>.", "<ENBT:number:tagName>", "<ENBT:integer:tagName>",
				"<ENBT:logic:tagName>", "<ENBT:text:tagName>", "<BNBT:number:tagName>", "<BNBT:integer:tagName>",
				"<BNBT:logic:tagName>", "<BNBT:text:tagName>", "<energy>", "<fluidlevel>" });
		name.setEditable(true);

		for (VariableElement var2 : editor.mcreator.getWorkspace().getVariableElements()) {
			name.addItem("<VAR:" + var2.getName() + ">");
			if (var2.getType() == VariableElementType.NUMBER)
				name.addItem("<VAR:integer:" + var2.getName() + ">");
		}

		ProcedureSelector displayCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/label_display_condition"), editor.mcreator, L10N.t("dialog.gui.label_event_display_condition"),
				ProcedureSelector.Side.CLIENT, false, VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		displayCondition.refreshList();

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));
		options.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.gui.label_text"), name));
		add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerAndEastElement(options, displayCondition, 20, 5)));

		setTitle(L10N.t("dialog.gui.label_component_title"));

		final JColor cola = new JColor(editor.mcreator);

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
			name.setSelectedItem(label.name);
			cola.setColor(label.color);
			displayCondition.setSelectedProcedure(label.displayCondition);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			setVisible(false);
			String text = (String) name.getSelectedItem();
			if (text != null) {
				if (label == null) {
					int textwidth = (int) (WYSIWYG.fontMC.getStringBounds(text, WYSIWYG.frc).getWidth());
					editor.editor.setPositioningMode(textwidth, 16);
					editor.editor.setPositionDefinedListener(e -> editor.editor.addComponent(
							new Label(text, editor.editor.newlyAddedComponentPosX,
									editor.editor.newlyAddedComponentPosY, text, cola.getColor(),
									displayCondition.getSelectedProcedure())));
				} else {
					int idx = editor.components.indexOf(label);
					editor.components.remove(label);
					Label labelNew = new Label(text, label.getX(), label.getY(), text, cola.getColor(),
							displayCondition.getSelectedProcedure());
					editor.components.add(idx, labelNew);
				}
			}
		});

		setVisible(true);
	}

}
