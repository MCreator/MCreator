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
import net.mcreator.element.parts.gui.Image;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.minecraft.ProcedureSelector;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableElementType;
import org.apache.commons.io.FilenameUtils;
import javax.annotation.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ImageDialog extends AbstractWYSIWYGDialog {

	public ImageDialog(WYSIWYGEditor editor, @Nullable Image image) {
		super(editor.mcreator, image);
		setSize(560, 180);
		setLocationRelativeTo(editor.mcreator);
		setModal(true);

		VComboBox<String> textureSelector = new SearchableComboBox<>(
				editor.mcreator.getFolderManager().getOtherTexturesList().stream().map(File::getName)
						.toArray(String[]::new));
		textureSelector.setRenderer(new WTextureComboBoxRenderer.OtherTextures(editor.mcreator.getWorkspace()));

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));
		options.add(PanelUtils.westAndCenterElement(L10N.label("dialog.gui.image_texture"), textureSelector));

		JCheckBox scale1x = L10N.checkbox("dialog.gui.image_use_scale");
		options.add(PanelUtils.join(FlowLayout.LEFT, scale1x));

		ProcedureSelector displayCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/image_display_condition"), editor.mcreator,
				L10N.t("dialog.gui.image_display_condition"), ProcedureSelector.Side.CLIENT, false,
				VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		displayCondition.refreshList();

		add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerAndEastElement(options, displayCondition, 20, 5)));

		setTitle(L10N.t("dialog.gui.image_title"));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));

		getRootPane().setDefaultButton(ok);

		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		if (image != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			textureSelector.setSelectedItem(image.image);
			scale1x.setSelected(image.use1Xscale);
			displayCondition.setSelectedProcedure(image.displayCondition);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			setVisible(false);
			String imageTxt = textureSelector.getSelectedItem();
			if (imageTxt != null) {
				if (image == null) {
					ImageIcon a = new ImageIcon(editor.mcreator.getFolderManager()
							.getOtherTextureFile(FilenameUtils.removeExtension(imageTxt)).getAbsolutePath());

					if (scale1x.isSelected())
						editor.editor.setPositioningMode(a.getIconWidth() / 2, a.getIconHeight() / 2);
					else
						editor.editor.setPositioningMode(a.getIconWidth(), a.getIconHeight());

					editor.editor.setPositionDefinedListener(e -> editor.editor.addComponent(setEditingComponent(
							new Image(imageTxt, editor.editor.newlyAddedComponentPosX,
									editor.editor.newlyAddedComponentPosY, imageTxt, scale1x.isSelected(),
									displayCondition.getSelectedProcedure()))));
				} else {
					int idx = editor.components.indexOf(image);
					editor.components.remove(image);
					Image labelNew = new Image(imageTxt, image.getX(), image.getY(), imageTxt, scale1x.isSelected(),
							displayCondition.getSelectedProcedure());
					editor.components.add(idx, labelNew);
					setEditingComponent(labelNew);
				}
			}
		});

		setVisible(true);
	}

}
