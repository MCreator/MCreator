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

import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.awt.*;

public class AnimationSettings extends JPanel {

	private final JSpinner frameDuration = new JSpinner(new SpinnerNumberModel(2, 1, 10000, 1));
	private final JCheckBox interpolate = new JCheckBox();

	public AnimationSettings() {
		setLayout(new GridLayout(2, 2, 15, 20));
		add(L10N.label("dialog.animation_maker.frame_duration"));
		add(frameDuration);
		add(L10N.label("dialog.animation_maker.interpolate_frame"));
		interpolate.setOpaque(false);
		add(interpolate);

		ComponentUtils.makeSection(this, L10N.t("dialog.animation_maker.settings"));
	}

	public int getFrameDuration() {
		return (int) frameDuration.getValue();
	}

	public boolean doesInterpolate() {
		return interpolate.isSelected();
	}
}