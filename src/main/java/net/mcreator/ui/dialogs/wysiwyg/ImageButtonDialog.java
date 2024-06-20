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
import net.mcreator.element.parts.gui.ImageButton;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class ImageButtonDialog extends AbstractWYSIWYGDialog<ImageButton> {

	public ImageButtonDialog(WYSIWYGEditor editor, @Nullable ImageButton button) {
		super(editor, button);
		setModal(true);
		setSize(540, 310);
		setLocationRelativeTo(editor.mcreator);
		setTitle(L10N.t("dialog.gui.image_button_add_title"));

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		TextureComboBox textureSelector = new TextureComboBox(editor.mcreator, TextureType.SCREEN, false);
		TextureComboBox hoveredTextureSelector = new TextureComboBox(editor.mcreator, TextureType.SCREEN, true);

		hoveredTextureSelector.setValidator(() -> {
			// The first image can never be null as this is the reference
			if (!textureSelector.hasTexture())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("validator.image_size.empty"));

			if (!hoveredTextureSelector.hasTexture())
				return Validator.ValidationResult.PASSED;

			// Finally, we can check if both images have the same height and width
			ImageIcon image1 = textureSelector.getTexture().getTextureIcon(editor.mcreator.getWorkspace());
			ImageIcon image2 = hoveredTextureSelector.getTexture().getTextureIcon(editor.mcreator.getWorkspace());

			if (ImageUtils.checkIfSameSize(image1.getImage(), image2.getImage()))
				return Validator.ValidationResult.PASSED;
			else
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("validator.image_size"));
		});
		hoveredTextureSelector.getComboBox().enableRealtimeValidation();

		add("North", PanelUtils.centerInPanel(L10N.label("dialog.gui.image_button_size")));

		options.add(PanelUtils.northAndCenterElement(
				PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.gui.image_texture"), textureSelector),
				PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.gui.hovered_image_texture"),
						hoveredTextureSelector)));

		ProcedureSelector onClick = new ProcedureSelector(IHelpContext.NONE.withEntry("gui/on_button_clicked"),
				editor.mcreator, L10N.t("dialog.gui.button_event_on_clicked"), ProcedureSelector.Side.BOTH, false,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		onClick.refreshList();

		ProcedureSelector displayCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/button_display_condition"), editor.mcreator,
				L10N.t("dialog.gui.button_display_condition"), ProcedureSelector.Side.CLIENT, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		displayCondition.refreshList();

		options.add(new JEmptyBox(20, 20));

		options.add(PanelUtils.gridElements(1, 2, 5, 5, onClick, displayCondition));

		add("Center", new JScrollPane(PanelUtils.centerInPanel(options)));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		getRootPane().setDefaultButton(ok);

		if (button != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			textureSelector.setTextureFromTextureName(button.image);
			hoveredTextureSelector.setTextureFromTextureName(button.hoveredImage);
			onClick.setSelectedProcedure(button.onClick);
			displayCondition.setSelectedProcedure(button.displayCondition);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			if (hoveredTextureSelector.getValidationStatus().getValidationResultType()
					!= Validator.ValidationResultType.ERROR) {
				setVisible(false);
				if (textureSelector.hasTexture()) {
					if (button == null) {
						String name = textToMachineName(editor.getComponentList(), "imagebutton_",
								textureSelector.getTexture().getTextureName().toLowerCase(Locale.ENGLISH));

						ImageButton component = new ImageButton(name, 0, 0, textureSelector.getTextureName(),
								hoveredTextureSelector.getTextureName(), onClick.getSelectedProcedure(),
								displayCondition.getSelectedProcedure());

						setEditingComponent(component);
						editor.editor.addComponent(component);
						editor.list.setSelectedValue(component, true);
						editor.editor.moveMode();
					} else {
						int idx = editor.components.indexOf(button);
						editor.components.remove(button);
						ImageButton buttonNew = new ImageButton(button.name, button.getX(), button.getY(),
								textureSelector.getTextureName(), hoveredTextureSelector.getTextureName(),
								onClick.getSelectedProcedure(), displayCondition.getSelectedProcedure());
						editor.components.add(idx, buttonNew);
						setEditingComponent(buttonNew);
					}
				}
			}
		});

		setVisible(true);
	}
}
