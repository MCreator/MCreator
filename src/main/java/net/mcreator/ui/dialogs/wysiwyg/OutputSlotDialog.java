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
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.OutputSlot;
import net.mcreator.element.parts.gui.Slot;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.ProcedureSelector;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class OutputSlotDialog extends MCreatorDialog {

	public OutputSlotDialog(WYSIWYGEditor editor, @Nullable OutputSlot slot) {
		super(editor.mcreator);
		setModal(true);
		setSize(850, 310);
		setLocationRelativeTo(editor.mcreator);

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		VTextField slotID = new VTextField(20);
		slotID.setPreferredSize(new Dimension(45, 28));
		slotID.enableRealtimeValidation();
		slotID.setValidator(() -> {
			try {
				int slotIDnum = Integer.parseInt(slotID.getText().trim());
				for (int i = 0; i < editor.list.getModel().getSize(); i++) {
					GUIComponent component = editor.list.getModel().getElementAt(i);
					if (slot != null && component instanceof Slot
							&& ((Slot) component).id == slot.id) // skip current element if edit mode
						continue;
					if (component instanceof Slot && component.name.equals("Slot #" + slotIDnum))
						return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
								L10N.t("dialog.slot.slot_id_already_used"));
				}
			} catch (Exception exc) {
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR, L10N.t("dialog.slot.slot_id_must_be_number"));
			}
			return new Validator.ValidationResult(Validator.ValidationResultType.PASSED, "");
		});
		slotID.setText("0");
		options.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.slot.slot_id"), slotID));

		JCheckBox disableStackInteraction = L10N.checkbox("dialog.slot.disable_player_interaction");
		options.add(PanelUtils.join(FlowLayout.LEFT, disableStackInteraction));

		JCheckBox dropItemsWhenNotBound = L10N.checkbox("dialog.slot.drop_item_when_gui_closed");
		options.add(PanelUtils.join(FlowLayout.LEFT, dropItemsWhenNotBound));

		dropItemsWhenNotBound.setSelected(true);

		final JColor color = new JColor(editor.mcreator);
		options.add(PanelUtils
				.join(FlowLayout.LEFT, L10N.label("dialog.slot.custom_color"),
						color));

		ProcedureSelector eh = new ProcedureSelector(IHelpContext.NONE.withEntry("gui/when_slot_changed"),
				editor.mcreator, L10N.t("dialog.slot.event_slot_content_changes"), ProcedureSelector.Side.BOTH, false,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		eh.refreshList();

		ProcedureSelector eh2 = new ProcedureSelector(IHelpContext.NONE.withEntry("gui/when_slot_item_taken"),
				editor.mcreator, L10N.t("dialog.slot.event_item_taken_from_slot"), ProcedureSelector.Side.BOTH, false,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		eh2.refreshList();

		ProcedureSelector eh3 = new ProcedureSelector(IHelpContext.NONE.withEntry("gui/when_transferred_from_slot"),
				editor.mcreator, L10N.t("dialog.slot.event_transfered_from_slot"), ProcedureSelector.Side.BOTH, false,
				Dependency
						.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map/amount:number"));
		eh3.refreshList();

		add("Center", new JScrollPane(PanelUtils.centerInPanel(PanelUtils.gridElements(1, 3, 5, 5, eh, eh2, eh3))));

		add("North", PanelUtils.join(FlowLayout.LEFT, options));
		setTitle(L10N.t("dialog.slot.output_editor_title"));
		JButton ok = L10N.button("dialog.slot.save_slot");
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		getRootPane().setDefaultButton(ok);

		if (slot != null) {
			ok.setText(L10N.t("dialog.slot.save_changes"));
			slotID.setText(String.valueOf(slot.id));
			color.setColor(slot.color);
			eh.setSelectedProcedure(slot.onSlotChanged);
			eh2.setSelectedProcedure(slot.onTakenFromSlot);
			eh3.setSelectedProcedure(slot.onStackTransfer);
			disableStackInteraction.setSelected(slot.disableStackInteraction);
			dropItemsWhenNotBound.setSelected(slot.dropItemsWhenNotBound);
		} else {
			int freeslotid = -1;
			for (int i = 0; i < editor.components.size(); i++) {
				GUIComponent component = editor.components.get(i);
				if (component instanceof Slot) {
					int slotid = ((Slot) component).id;
					if (slotid > freeslotid)
						freeslotid = slotid;
				}
			}
			slotID.setText("" + ++freeslotid);
		}

		cancel.addActionListener(event -> setVisible(false));
		ok.addActionListener(event -> {
			if (slotID.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				setVisible(false);
				int slotIDnum = Integer.parseInt(slotID.getText().trim());
				if (slot == null) {
					editor.lol.setSelectedIndex(1);
					editor.editor.setPositioningMode(18, 18);
					editor.editor.setPositionDefinedListener(e1 -> editor.editor.addComponent(
							new OutputSlot(slotIDnum, "Slot #" + slotIDnum, editor.editor.newlyAddedComponentPosX,
									editor.editor.newlyAddedComponentPosY,
									color.getColor().equals(Color.white) ? null : color.getColor(),
									disableStackInteraction.isSelected(), dropItemsWhenNotBound.isSelected(),
									eh.getSelectedProcedure(), eh2.getSelectedProcedure(),
									eh3.getSelectedProcedure())));
				} else {
					int idx = editor.components.indexOf(slot);
					editor.components.remove(slot);
					OutputSlot slotNew = new OutputSlot(slotIDnum, "Slot #" + slotIDnum, slot.getX(), slot.getY(),
							color.getColor().equals(Color.white) ? null : color.getColor(),
							disableStackInteraction.isSelected(), dropItemsWhenNotBound.isSelected(),
							eh.getSelectedProcedure(), eh2.getSelectedProcedure(), eh3.getSelectedProcedure());
					editor.components.add(idx, slotNew);
				}
			}
		});

		setVisible(true);
	}

}
