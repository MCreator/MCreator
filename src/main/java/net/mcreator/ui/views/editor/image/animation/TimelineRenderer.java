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
import net.mcreator.ui.views.AnimationMakerView;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;

public class TimelineRenderer extends JPanel implements ListCellRenderer<Canvas> {

	private final AnimationMakerView timeline;

	public TimelineRenderer(AnimationMakerView timeline) {
		this.timeline = timeline;
		setLayout(new BorderLayout()); // Allow to remove the default offset of the image
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Canvas> list,
			Canvas value, int index, boolean isSelected, boolean cellHasFocus) {
		removeAll();
		if (cellHasFocus) {
			setBackground(PreferencesManager.PREFERENCES.ui.interfaceAccentColor.get());
			timeline.changeFrame(value);
		} else if (isSelected) {
			setBackground(PreferencesManager.PREFERENCES.imageEditor.selectedFramesColor.get());
		} else {
			setBackground(Color.gray);
		}
		setPreferredSize(new Dimension(170, 170));
		add(new JLabel(new ImageIcon(ImageUtils.resize(value.render(), 170))));

		return this;
	}

}