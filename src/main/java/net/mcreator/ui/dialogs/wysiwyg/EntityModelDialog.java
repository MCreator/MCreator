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
import net.mcreator.element.parts.gui.EntityModel;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;

public class EntityModelDialog extends AbstractWYSIWYGDialog<EntityModel> {

	public EntityModelDialog(WYSIWYGEditor editor, @Nullable EntityModel model) {
		super(editor, model);
		setModal(true);
		setSize(480, 200);
		setLocationRelativeTo(editor.mcreator);
		setTitle(L10N.t("dialog.gui.add_entity_model"));

		JPanel options = new JPanel();
		JPanel parameters = new JPanel();
		parameters.setLayout(new BoxLayout(parameters, BoxLayout.PAGE_AXIS));
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		ProcedureSelector entityModel = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/entity_model"), editor.mcreator,
				L10N.t("dialog.gui.entity_model_procedure"), ProcedureSelector.Side.CLIENT, false,
				VariableTypeLoader.BuiltInTypes.ENTITY,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		entityModel.refreshList();

		ProcedureSelector displayCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/entity_model_display_condition"), editor.mcreator,
				L10N.t("dialog.gui.model_display_condition"), ProcedureSelector.Side.CLIENT, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		displayCondition.refreshList();

		JSpinner scale = new JSpinner(new SpinnerNumberModel(30, 0.1, 100, 0.1));
		JCheckBox followMouseMovement = new JCheckBox();
		followMouseMovement.setOpaque(false);
		followMouseMovement.setEnabled(editor.isNotOverlayType);

		parameters.add(PanelUtils.join(entityModel, displayCondition));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));

		getRootPane().setDefaultButton(ok);

		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));

		parameters.add(PanelUtils.centerInPanel
				(PanelUtils.join(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("gui/entity_model_scale"),
				L10N.label("dialog.gui.model_scale")), scale,
				(PanelUtils.join(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("gui/entity_model_follow_mouse"),
				L10N.label("dialog.gui.model_follow_mouse")), followMouseMovement)))));

		options.add("Center", parameters);
		options.add("South", PanelUtils.join(ok, cancel));

		add("Center", options);

		if (model != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			entityModel.setSelectedProcedure(model.entityModel);
			displayCondition.setSelectedProcedure(model.displayCondition);
			scale.setValue(model.scale);
			followMouseMovement.setSelected(model.followMouseMovement);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			Procedure entModel = entityModel.getSelectedProcedure();
			if (entModel != null) {
				setVisible(false);
				if (model == null) {
					EntityModel component = new EntityModel(0, 0, entityModel.getSelectedProcedure(),
							displayCondition.getSelectedProcedure(), (double) scale.getValue(), followMouseMovement.isSelected());
					setEditingComponent(component);
					editor.editor.addComponent(component);
					editor.list.setSelectedValue(component, true);
					editor.editor.moveMode();
				} else {
					int idx = editor.components.indexOf(model);
					editor.components.remove(model);
					EntityModel modelNew = new EntityModel(model.getX(), model.getY(), entityModel.getSelectedProcedure(),
							displayCondition.getSelectedProcedure(), (double) scale.getValue(), followMouseMovement.isSelected());
					editor.components.add(idx, modelNew);
					setEditingComponent(modelNew);
				}
			} else {
				StringBuilder stringBuilder = new StringBuilder(L10N.t("dialog.gui.procedure_required"));
				JOptionPane.showMessageDialog(editor.mcreator, stringBuilder.toString(),
						L10N.t("dialog.gui.no_procedure_selected"), JOptionPane.WARNING_MESSAGE);
			}
		});

		setVisible(true);
	}

}
