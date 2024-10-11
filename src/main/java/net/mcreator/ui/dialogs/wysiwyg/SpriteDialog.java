/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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
import net.mcreator.element.parts.gui.Sprite;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.procedure.NumberProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class SpriteDialog extends AbstractWYSIWYGDialog<Sprite> {
	public SpriteDialog(WYSIWYGEditor editor, @Nullable Sprite sprite) {
		super(editor, sprite);
		setSize(680, 170);
		setLocationRelativeTo(editor.mcreator);
		setModal(true);
		setTitle(L10N.t("dialog.gui.sprite_title"));

		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

		JPanel options = new JPanel(new BorderLayout());

		TextureComboBox textureSelector = new TextureComboBox(editor.mcreator, TextureType.SCREEN, false);

		SpinnerNumberModel spritesCountModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
		JSpinner spritesCount = new JSpinner(spritesCountModel);
		spritesCount.setPreferredSize(new Dimension(80, spritesCount.getPreferredSize().height));

		options.add("Center", PanelUtils.centerAndSouthElement(
				PanelUtils.westAndCenterElement(L10N.label("dialog.gui.image_texture"), textureSelector),
				PanelUtils.westAndCenterElement(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("gui/sprite_count"),
						L10N.label("dialog.gui.sprite_count")), spritesCount), 5, 5));

		final JComboBox<GUIComponent.AnchorPoint> anchor = new JComboBox<>(GUIComponent.AnchorPoint.values());
		anchor.setSelectedItem(GUIComponent.AnchorPoint.CENTER);
		if (!editor.isNotOverlayType) {
			options.add("South", PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.gui.anchor"), anchor));
		}

		ProcedureSelector displayCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/sprite_display_condition"), editor.mcreator,
				L10N.t("dialog.gui.sprite_display_condition"), ProcedureSelector.Side.CLIENT, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		displayCondition.refreshList();

		SpinnerNumberModel model = new SpinnerNumberModel(0, 0, (int) spritesCount.getValue() - 1, 1);
		JSpinner spinner = new JSpinner(model);

		final int[] previousSpritesCount = { (int) spritesCount.getValue() };
		spritesCount.addChangeListener(e -> {
			int currentSpritesCount = (int) spritesCount.getValue();

			model.setMaximum(currentSpritesCount - 1);
			if (previousSpritesCount[0] > currentSpritesCount && model.getNumber().intValue() == previousSpritesCount[0] - 1)
				model.setValue(model.getNumber().intValue() - 1);
			previousSpritesCount[0] = currentSpritesCount;
		});

		textureSelector.getComboBox().addActionListener(e -> {
			if (textureSelector.getTexture() != null) {
				ImageIcon selectedTexture = textureSelector.getTexture().getTextureIcon(editor.mcreator.getWorkspace());
				int maximum = Math.max(selectedTexture.getIconWidth(), selectedTexture.getIconHeight());

				spritesCountModel.setMaximum(maximum);
				if (maximum < spritesCountModel.getNumber().intValue())
					spritesCountModel.setValue(maximum);

				if (model.getNumber().intValue() > maximum)
					model.setValue(maximum - 1);
			}
		});

		NumberProcedureSelector spriteIndex = new NumberProcedureSelector(
				IHelpContext.NONE.withEntry("gui/sprite_index"), editor.mcreator,
				L10N.t("dialog.gui.sprite_index"), ProcedureSelector.Side.CLIENT, false,
				spinner, 80,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		spriteIndex.refreshList();

		add("East", PanelUtils.northAndCenterElement(displayCondition, PanelUtils.centerInPanel(spriteIndex)));
		add("Center", PanelUtils.join(FlowLayout.LEFT, options));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));

		getRootPane().setDefaultButton(ok);

		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		if (sprite != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			textureSelector.setTextureFromTextureName(sprite.sprite);
			spritesCount.setValue(sprite.spritesCount);
			displayCondition.setSelectedProcedure(sprite.displayCondition);
			spriteIndex.setSelectedProcedure(sprite.spriteIndex);
			anchor.setSelectedItem(sprite.anchorPoint);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			setVisible(false);
			if (textureSelector.hasTexture()) {
				if (sprite == null) {
					Sprite component = new Sprite(0, 0, textureSelector.getTextureName(), (int) spritesCount.getValue(),
							displayCondition.getSelectedProcedure(), spriteIndex.getSelectedProcedure());
					if (!editor.isNotOverlayType)
						component.anchorPoint = (GUIComponent.AnchorPoint) anchor.getSelectedItem();
					setEditingComponent(component);
					editor.editor.addComponent(component);
					editor.list.setSelectedValue(component, true);
					editor.editor.moveMode();
				} else {
					int idx = editor.components.indexOf(sprite);
					editor.components.remove(sprite);
					Sprite spriteNew = new Sprite(sprite.getX(), sprite.getY(), textureSelector.getTextureName(), (int) spritesCount.getValue(),
							displayCondition.getSelectedProcedure(), spriteIndex.getSelectedProcedure());
					if (!editor.isNotOverlayType)
						spriteNew.anchorPoint = (GUIComponent.AnchorPoint) anchor.getSelectedItem();
					editor.components.add(idx, spriteNew);
					setEditingComponent(spriteNew);
				}
			}
		});

		setVisible(true);
	}
}
