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
import net.mcreator.element.parts.gui.Image;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ImageDialog extends AbstractWYSIWYGDialog<Image> {

	public ImageDialog(WYSIWYGEditor editor, @Nullable Image image) {
		super(editor, image);
		setSize(560, 180);
		setLocationRelativeTo(editor.mcreator);
		setModal(true);

		VComboBox<String> textureSelector = new SearchableComboBox<>(
				editor.mcreator.getFolderManager().getTexturesList(TextureType.SCREEN).stream().map(File::getName)
						.toArray(String[]::new));
		textureSelector.setRenderer(
				new WTextureComboBoxRenderer.TypeTextures(editor.mcreator.getWorkspace(), TextureType.SCREEN));

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));
		options.add(PanelUtils.westAndCenterElement(L10N.label("dialog.gui.image_texture"), textureSelector));

		JCheckBox scale1x = L10N.checkbox("dialog.gui.image_use_scale");
		options.add(PanelUtils.join(FlowLayout.LEFT, scale1x));

		final JComboBox<GUIComponent.AnchorPoint> anchor = new JComboBox<>(GUIComponent.AnchorPoint.values());
		anchor.setSelectedItem(GUIComponent.AnchorPoint.CENTER);
		if (!editor.isNotOverlayType) {
			options.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.gui.anchor"), anchor));
		}

		ProcedureSelector displayCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/image_display_condition"), editor.mcreator,
				L10N.t("dialog.gui.image_display_condition"), ProcedureSelector.Side.CLIENT, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
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
			anchor.setSelectedItem(image.anchorPoint);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			setVisible(false);
			String imageTxt = textureSelector.getSelectedItem();
			if (imageTxt != null) {
				if (image == null) {
					Image component = new Image(0, 0, imageTxt, scale1x.isSelected(),
							displayCondition.getSelectedProcedure());
					if (!editor.isNotOverlayType)
						component.anchorPoint = (GUIComponent.AnchorPoint) anchor.getSelectedItem();
					setEditingComponent(component);
					editor.editor.addComponent(component);
					editor.list.setSelectedValue(component, true);
					editor.editor.moveMode();
				} else {
					int idx = editor.components.indexOf(image);
					editor.components.remove(image);
					Image imageNew = new Image(image.getX(), image.getY(), imageTxt, scale1x.isSelected(),
							displayCondition.getSelectedProcedure());
					if (!editor.isNotOverlayType)
						imageNew.anchorPoint = (GUIComponent.AnchorPoint) anchor.getSelectedItem();
					editor.components.add(idx, imageNew);
					setEditingComponent(imageNew);
				}
			}
		});

		setVisible(true);
	}

}
