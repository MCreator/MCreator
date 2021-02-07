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

package net.mcreator.ui.minecraft;

import net.mcreator.element.parts.Sound;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.util.ListUtils;
import net.mcreator.util.SoundUtils;
import net.mcreator.workspace.elements.SoundElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;

public class SoundSelector extends JPanel {

	private final VTextField tfe = new VTextField(14);
	private final JButton bt = new JButton("...");
	private final JButton rm = new JButton(UIRES.get("18px.remove"));
	private final JButton play = new JButton(UIRES.get("16px.play"));

	private final MCreator mcreator;

	public SoundSelector(MCreator frame) {
		this.mcreator = frame;

		setOpaque(false);
		bt.addActionListener(event -> {
			String[] sounds = ElementUtil.getAllSounds(frame.getWorkspace());
			Arrays.sort(sounds);
			String s = (String) JOptionPane.showInputDialog(frame, L10N.t("dialog.selector.sound_message"),
					L10N.t("dialog.selector.sound_title"), JOptionPane.PLAIN_MESSAGE, null, sounds, sounds[0]);
			setSound(s);
		});
		rm.addActionListener(e -> setSound((String) null));
		tfe.setEditable(false);
		ComponentUtils.deriveFont(tfe, 16);
		bt.setOpaque(false);

		rm.setOpaque(false);
		rm.setMargin(new Insets(0, 3, 0, 3));

		play.setOpaque(false);
		play.setMargin(new Insets(0, 0, 0, 0));
		play.setContentAreaFilled(false);

		play.setVisible(false);
		rm.setEnabled(false);

		play.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent me) {
				SoundElement soundElement = frame.getWorkspace().getSoundElements().stream()
						.filter(e -> e.getName().equals(getSound().getUnmappedValue().replaceFirst("CUSTOM:", "")))
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

		setLayout(new BorderLayout(0, 0));

		add("West", play);
		add("Center", tfe);
		add("East", PanelUtils.gridElements(1, 2, bt, rm));
	}

	@Override public void setEnabled(boolean enabled) {
		tfe.setEnabled(enabled);
		bt.setEnabled(enabled);
		rm.setEnabled(enabled);
	}

	public VTextField getVTextField() {
		return tfe;
	}

	public Sound getSound() {
		return new Sound(mcreator.getWorkspace(), tfe.getText());
	}

	public void setSound(Sound sound) {
		if (sound != null)
			this.setSound(sound.getUnmappedValue());
		else
			this.setSound((String) null);
	}

	public void setSound(String sound) {
		tfe.setText(sound);
		tfe.getValidationStatus();

		if (sound != null && !sound.equals("")) {
			play.setVisible(sound.startsWith("CUSTOM:"));
			rm.setEnabled(true);
		} else {
			play.setVisible(false);
			rm.setEnabled(false);
		}
	}

	public void setText(String text) {
		this.setSound(text);
	}

}
