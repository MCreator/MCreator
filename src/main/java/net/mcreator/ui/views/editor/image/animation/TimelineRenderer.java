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

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.AnimationMakerView;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;

public class TimelineRenderer extends JPanel implements ListCellRenderer<AnimationMakerView.AnimationFrame> {

	public TimelineRenderer() {
		setLayout(new BorderLayout()); // Allow to remove the default offset of the image
		setPreferredSize(new Dimension(170, 170));
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends AnimationMakerView.AnimationFrame> list,
			AnimationMakerView.AnimationFrame value, int index, boolean isSelected, boolean cellHasFocus) {
		removeAll();
		if (cellHasFocus) {
			setBackground(Theme.current().getInterfaceAccentColor());
		} else if (isSelected) {
			setBackground(PreferencesManager.PREFERENCES.imageEditor.selectedFramesColor.get());
		} else {
			setBackground(Color.gray);
		}
		add(new JLabel(new ImageIcon(ImageUtils.resize(value.getImage(), 170))));

		return this;
	}

}