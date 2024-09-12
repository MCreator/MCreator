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
import net.mcreator.ui.component.TranslatedComboBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.Map;

public class SpriteDialog extends AbstractWYSIWYGDialog<Sprite> {
	public SpriteDialog(WYSIWYGEditor editor, @Nullable Sprite sprite) {
		super(editor, sprite);
		setSize(660, 250);
		setLocationRelativeTo(editor.mcreator);
		setModal(true);
		setTitle(L10N.t("dialog.gui.sprite_title"));

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		TextureComboBox textureSelector = new TextureComboBox(editor.mcreator, TextureType.SCREEN, false);
		TranslatedComboBox spriteDirection = new TranslatedComboBox(
				Map.entry("Horizontal", "dialog.gui.sprite_direction_horizontal"),
				Map.entry("Vertical", "dialog.gui.sprite_direction_vertical"));

		options.add(PanelUtils.join(L10N.label("dialog.gui.image_texture"), textureSelector));
		options.add(PanelUtils.join(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("gui/sprite_direction"),
				L10N.label("dialog.gui.sprite_direction")), spriteDirection));

		final JComboBox<GUIComponent.AnchorPoint> anchor = new JComboBox<>(GUIComponent.AnchorPoint.values());
		anchor.setSelectedItem(GUIComponent.AnchorPoint.CENTER);
		if (!editor.isNotOverlayType) {
			options.add(PanelUtils.join(L10N.label("dialog.gui.anchor"), anchor));
		}

		ProcedureSelector displayCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/sprite_display_condition"), editor.mcreator,
				L10N.t("dialog.gui.sprite_display_condition"), ProcedureSelector.Side.CLIENT, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		displayCondition.refreshList();

		ProcedureSelector spriteDisplayedSize = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/sprite_displayed_size"), editor.mcreator,
				L10N.t("dialog.gui.sprite_displayed_size"), ProcedureSelector.Side.CLIENT, false,
				VariableTypeLoader.BuiltInTypes.NUMBER,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		spriteDisplayedSize.refreshList();

		options.add(PanelUtils.gridElements(1, 2, 5, 5, displayCondition, spriteDisplayedSize));

		add("Center", options);

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));

		getRootPane().setDefaultButton(ok);

		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		if (sprite != null) {
			ok.setText(L10N.t("dialog.common.save_changes"));
			textureSelector.setTextureFromTextureName(sprite.sprite);
			spriteDirection.setSelectedItem(sprite.spriteDirection);
			displayCondition.setSelectedProcedure(sprite.displayCondition);
			spriteDisplayedSize.setSelectedProcedure(sprite.spriteDisplayedSize);
			anchor.setSelectedItem(sprite.anchorPoint);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			setVisible(false);
			if (textureSelector.hasTexture()) {
				if (sprite == null) {
					Sprite component = new Sprite(0, 0, textureSelector.getTextureName(), spriteDirection.getSelectedItem(),
							displayCondition.getSelectedProcedure(), spriteDisplayedSize.getSelectedProcedure());
					if (!editor.isNotOverlayType)
						component.anchorPoint = (GUIComponent.AnchorPoint) anchor.getSelectedItem();
					setEditingComponent(component);
					editor.editor.addComponent(component);
					editor.list.setSelectedValue(component, true);
					editor.editor.moveMode();
				} else {
					int idx = editor.components.indexOf(sprite);
					editor.components.remove(sprite);
					Sprite spriteNew = new Sprite(sprite.getX(), sprite.getY(), textureSelector.getTextureName(), spriteDirection.getSelectedItem(),
							displayCondition.getSelectedProcedure(), spriteDisplayedSize.getSelectedProcedure());
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
