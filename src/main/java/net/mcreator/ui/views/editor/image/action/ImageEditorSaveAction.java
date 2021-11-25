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

package net.mcreator.ui.views.editor.image.action;

import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.views.editor.image.ImageMakerView;

import javax.swing.*;

public class ImageEditorSaveAction extends BasicAction {
	public ImageEditorSaveAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.image_editor.save"), actionEvent -> {
			JPanel pan = actionRegistry.getMCreator().mcreatorTabs.getCurrentTab().getContent();
			if (pan instanceof ImageMakerView imageMakerView) {
				imageMakerView.save();
			}
		});
		setTooltip(L10N.t("action.image_editor.save.tooltip"));
		actionRegistry.getMCreator().mcreatorTabs.addTabShownListener(
				tab -> setEnabled(tab.getContent() instanceof ImageMakerView));
	}
}
