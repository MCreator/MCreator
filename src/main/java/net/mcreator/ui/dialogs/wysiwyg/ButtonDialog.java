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

import net.mcreator.blockly.Dependency;
import net.mcreator.element.parts.gui.Button;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.minecraft.ProcedureSelector;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ButtonDialog extends MCreatorDialog {

	public ButtonDialog(WYSIWYGEditor editor, @Nullable Button button) {
		super(editor.mcreator);
		setModal(true);
		setSize(480, 200);
		setLocationRelativeTo(editor.mcreator);
		setTitle("Add button");
		JTextField nameField = new JTextField(20);
		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		if (button == null)
			add("North", PanelUtils.centerInPanel(
					new JLabel("After you click OK, right-click with the mouse to change button width")));
		else
			add("North", PanelUtils.centerInPanel(
					new JLabel("To resize the button, use move tool and right-click with the mouse when moving")));

		options.add(PanelUtils.join(new JLabel("Text of the button: "), nameField));

		ProcedureSelector eh = new ProcedureSelector(IHelpContext.NONE.withEntry("gui/on_button_clicked"),
				editor.mcreator, "On button clicked", ProcedureSelector.Side.BOTH, false,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		eh.refreshList();
		options.add(PanelUtils.join(eh));

		add("Center", options);
		JButton ok = new JButton("OK");
		JButton cancel = new JButton("Cancel");
		add("South", PanelUtils.join(ok, cancel));

		getRootPane().setDefaultButton(ok);

		if (button != null) {
			ok.setText("Save changes");
			nameField.setText(button.name);
			eh.setSelectedProcedure(button.onClick);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			setVisible(false);
			String text = nameField.getText();
			if (text != null && !text.equals("")) {
				if (button == null) {
					int textwidth = (int) (WYSIWYG.fontMC.getStringBounds(text, WYSIWYG.frc).getWidth());
					editor.editor.setPositioningMode(textwidth + 25, 20);
					editor.editor.setPositionDefinedListener(e -> editor.editor.addComponent(
							new Button(text, editor.editor.newlyAddedComponentPosX,
									editor.editor.newlyAddedComponentPosY, text, editor.editor.ow, editor.editor.oh,
									eh.getSelectedProcedure())));
				} else {
					int idx = editor.components.indexOf(button);
					editor.components.remove(button);
					Button buttonNew = new Button(text, button.getX(), button.getY(), text, button.width, button.height,
							eh.getSelectedProcedure());
					editor.components.add(idx, buttonNew);
				}
			}
		});

		setVisible(true);
	}

}
