/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

public class ImageEditorSwapColorsAction extends BasicAction {
	public ImageEditorSwapColorsAction(ActionRegistry actionRegistry) {
		super(actionRegistry, L10N.t("action.image_editor.color_selector.swap"), actionEvent -> {
			JPanel pan = actionRegistry.getMCreator().getTabs().getCurrentTab().getContent();
			if (pan instanceof ImageMakerView imageMakerView) {
				imageMakerView.getToolPanel().getColorSelector().swapColors();
			}
		});
		setTooltip(L10N.t("action.image_editor.color_selector.swap.tooltip"));
		actionRegistry.getMCreator().getTabs()
				.addTabShownListener(tab -> setEnabled(tab.getContent() instanceof ImageMakerView));
	}
}
