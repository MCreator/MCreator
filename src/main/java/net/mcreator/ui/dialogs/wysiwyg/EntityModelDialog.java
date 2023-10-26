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
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.validators.ProcedureSelectorValidator;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class EntityModelDialog extends AbstractWYSIWYGDialog<EntityModel> {

	public EntityModelDialog(WYSIWYGEditor editor, @Nullable EntityModel model) {
		super(editor, model);
		setModal(true);
		setSize(500, 270);
		setLocationRelativeTo(editor.mcreator);
		setTitle(L10N.t("dialog.gui.add_entity_model"));

		JPanel options = new JPanel(new BorderLayout(15, 15));

		ProcedureSelector entityModel = new ProcedureSelector(IHelpContext.NONE.withEntry("gui/entity_model"),
				editor.mcreator, L10N.t("dialog.gui.entity_model_procedure"), ProcedureSelector.Side.CLIENT, false,
				VariableTypeLoader.BuiltInTypes.ENTITY,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		entityModel.refreshList();
		entityModel.setValidator(new ProcedureSelectorValidator(entityModel));

		ProcedureSelector displayCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/entity_model_display_condition"), editor.mcreator,
				L10N.t("dialog.gui.model_display_condition"), ProcedureSelector.Side.CLIENT, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		displayCondition.refreshList();

		JSpinner scale = new JSpinner(new SpinnerNumberModel(30, 1, 100, 1));
		JSpinner rotationX = new JSpinner(new SpinnerNumberModel(0, -360, 360, 1));

		JCheckBox followMouseMovement = new JCheckBox();
		followMouseMovement.setOpaque(false);

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));

		getRootPane().setDefaultButton(ok);

		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));

		JPanel opts = new JPanel(new GridLayout(0, 2, 2, 2));

		opts.add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("gui/entity_model_scale"),
				L10N.label("dialog.gui.model_scale")));
		opts.add(scale);

		opts.add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("gui/entity_model_rotation"),
				L10N.label("dialog.gui.model_rotation_x")));
		opts.add(rotationX);

		if (editor.isNotOverlayType) {
			opts.add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("gui/entity_model_follow_mouse"),
					L10N.label("dialog.gui.model_follow_mouse")));
			opts.add(followMouseMovement);
		}

		JComboBox<GUIComponent.AnchorPoint> anchor = new JComboBox<>(GUIComponent.AnchorPoint.values());
		anchor.setSelectedItem(GUIComponent.AnchorPoint.CENTER);
		if (!editor.isNotOverlayType) {
			opts.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.gui.anchor")));
			opts.add(anchor);
		}

		options.add("North", PanelUtils.join(entityModel, displayCondition));
		options.add("Center", PanelUtils.join(FlowLayout.LEFT, opts));
		options.add("South", PanelUtils.join(ok, cancel));

		add("Center", options);

		if (model != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			entityModel.setSelectedProcedure(model.entityModel);
			displayCondition.setSelectedProcedure(model.displayCondition);
			scale.setValue(model.scale);
			rotationX.setValue(model.rotationX);
			followMouseMovement.setSelected(model.followMouseMovement);
			anchor.setSelectedItem(model.anchorPoint);
		}

		cancel.addActionListener(e -> setVisible(false));
		ok.addActionListener(e -> {
			if (entityModel.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				setVisible(false);
				if (model == null) {
					EntityModel component = new EntityModel(0, 0, entityModel.getSelectedProcedure(),
							displayCondition.getSelectedProcedure(), (int) scale.getValue(), (int) rotationX.getValue(),
							followMouseMovement.isSelected());
					if (!editor.isNotOverlayType)
						component.anchorPoint = (GUIComponent.AnchorPoint) anchor.getSelectedItem();
					setEditingComponent(component);
					editor.editor.addComponent(component);
					editor.list.setSelectedValue(component, true);
					editor.editor.moveMode();
				} else {
					int idx = editor.components.indexOf(model);
					editor.components.remove(model);
					EntityModel modelNew = new EntityModel(model.getX(), model.getY(),
							entityModel.getSelectedProcedure(), displayCondition.getSelectedProcedure(),
							(int) scale.getValue(), (int) rotationX.getValue(), followMouseMovement.isSelected());
					if (!editor.isNotOverlayType)
						modelNew.anchorPoint = (GUIComponent.AnchorPoint) anchor.getSelectedItem();
					editor.components.add(idx, modelNew);
					setEditingComponent(modelNew);
				}
			}
		});

		setVisible(true);
	}

}
