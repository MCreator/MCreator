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

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class SoundSelector extends JPanel {

	private final VTextField tfe = new VTextField(14);
	private final JButton bt = new JButton("...");
	private final JButton rm = new JButton(UIRES.get("18px.remove"));

	private final MCreator mcreator;

	public SoundSelector(MCreator frame) {
		this.mcreator = frame;

		setOpaque(false);
		bt.addActionListener(event -> {
			String[] sounds = ElementUtil.getAllSounds(frame.getWorkspace());
			Arrays.sort(sounds);

			String s = (String) JOptionPane
					.showInputDialog(frame, L10N.t("dialog.selector.sound_message"),
							L10N.t("dialog.selector.sound_title"),
							JOptionPane.PLAIN_MESSAGE, null, sounds, sounds[0]);
			tfe.setText(s);
			tfe.getValidationStatus();
		});
		rm.addActionListener(e -> tfe.setText(""));
		tfe.setEditable(false);
		ComponentUtils.deriveFont(tfe, 16);
		bt.setOpaque(false);
		rm.setOpaque(false);
		rm.setMargin(new Insets(0, 0, 0, 0));

		setLayout(new BorderLayout(0, 0));
		add("Center", tfe);
		add("East", PanelUtils.westAndEastElement(rm, bt));
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
			tfe.setText(sound.getUnmappedValue());
		else
			tfe.setText("");
	}

	public void setText(String text) {
		tfe.setText(text);
	}

}
