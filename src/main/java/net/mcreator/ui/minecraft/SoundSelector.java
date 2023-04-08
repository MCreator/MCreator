/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft;

import net.mcreator.element.parts.Sound;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JSelector;
import net.mcreator.ui.dialogs.StringSelectorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.ListUtils;
import net.mcreator.util.SoundUtils;
import net.mcreator.workspace.elements.SoundElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class SoundSelector extends JSelector<Sound> {

	private final JButton play = new JButton(UIRES.get("16px.play"));

	private final MCreator mcreator;

	public SoundSelector(MCreator frame) {
		super(false, false);
		this.mcreator = frame;

		play.setVisible(false);
		play.setOpaque(false);
		play.setMargin(new Insets(0, 0, 0, 0));
		play.setContentAreaFilled(false);
		play.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent me) {
				SoundElement soundElement = frame.getWorkspace().getSoundElements().stream()
						.filter(e -> e.getName().equals(getSelected().getUnmappedValue().replaceFirst("CUSTOM:", "")))
						.findFirst().orElse(null);
				if (soundElement != null) {
					if (!soundElement.getFiles().isEmpty()) {
						SoundUtils.playSound(new File(frame.getWorkspace().getFolderManager().getSoundsDir(),
								ListUtils.getRandomItem(soundElement.getFiles()) + ".ogg"));
						play.setEnabled(false);
					}
				}
			}

			@Override public void mouseReleased(MouseEvent e) {
				SoundUtils.stopAllSounds();
				play.setEnabled(true);
			}

		});

		add("West", play);
	}

	@Override public void btListener(ActionEvent event) {
		String s = StringSelectorDialog.openSelectorDialog(mcreator, ElementUtil::getAllSounds,
				L10N.t("dialog.selector.sound.title"), L10N.t("dialog.selector.sound.message"));
		if (s != null)
			setSelected(s);
	}

	@Override public void rmListener(ActionEvent event) {
		setSelected((String) null);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		play.setEnabled(enabled);
	}

	@Override public Sound getSelected() {
		return new Sound(mcreator.getWorkspace(), tfe.getText());
	}

	@Override public void setSelected(Sound selected) {
		if (selected != null)
			this.setSelected(selected.getUnmappedValue());
		else
			this.setSelected((String) null);
	}

	@Override public void setSelected(String selected) {
		tfe.setText(selected);
		tfe.getValidationStatus();

		if (selected != null && !selected.equals("")) {
			play.setVisible(selected.startsWith("CUSTOM:"));
			rm.setEnabled(true);
		} else {
			play.setVisible(false);
			rm.setEnabled(false);
		}
	}

}
