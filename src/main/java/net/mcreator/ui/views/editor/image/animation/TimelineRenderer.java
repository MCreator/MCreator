/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.views.editor.image.animation;

import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.AnimationMakerView;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntSupplier;

public class TimelineRenderer extends JLabel implements ListCellRenderer<AnimationMakerView.AnimationFrame> {

	private static final int borderThickness = 2;

	private static final Color currentFrameColor = new Color(75, 85, 197);

	private final IntSupplier currentFrameSupplier;

	public TimelineRenderer(IntSupplier currentFrameSupplier) {
		this.currentFrameSupplier = currentFrameSupplier;

		setPreferredSize(new Dimension(170, 170));
		setHorizontalAlignment(JLabel.CENTER);
		setVerticalAlignment(JLabel.CENTER);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends AnimationMakerView.AnimationFrame> list,
			AnimationMakerView.AnimationFrame value, int index, boolean isSelected, boolean cellHasFocus) {
		if (index == currentFrameSupplier.getAsInt()) {
			setOpaque(true);
			setBackground(currentFrameColor);
		} else {
			setOpaque(false);
		}

		if (isSelected) {
			setBorder(BorderFactory.createLineBorder(Theme.current().getInterfaceAccentColor(), borderThickness));
		} else {
			setBorder(BorderFactory.createEmptyBorder(borderThickness, borderThickness, borderThickness,
					borderThickness));
		}

		setIcon(new ImageIcon(ImageUtils.resize(value.getImage(), 170 - borderThickness * 2)));

		return this;
	}

}