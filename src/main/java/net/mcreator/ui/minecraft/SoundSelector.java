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
import net.mcreator.ui.component.TechnicalButton;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.StringSelectorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.component.VTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SoundSelector extends JPanel {

	private final VTextField tfe = new VTextField(14);
	private final JButton bt = new TechnicalButton(UIRES.get("18px.edit"));
	private final JButton rm = new TechnicalButton(UIRES.get("18px.remove"));
	private final List<ActionListener> listeners = new ArrayList<>();

	private final MCreator mcreator;

	public SoundSelector(MCreator frame) {
		setLayout(new BorderLayout(0, 0));
		setBackground(Theme.current().getBackgroundColor());

		this.mcreator = frame;

		bt.addActionListener(event -> {
			String s = StringSelectorDialog.openSelectorDialog(mcreator, ElementUtil::getAllSounds,
					L10N.t("dialog.selector.sound.title"), L10N.t("dialog.selector.sound.message"));
			if (s != null)
				setSound(s);
		});
		rm.addActionListener(e -> setSound((String) null));
		tfe.setEditable(false);
		tfe.setForeground(tfe.getDisabledTextColor());
		tfe.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					bt.doClick();
			}
		});
		ComponentUtils.deriveFont(tfe, 14);

		bt.setOpaque(false);
		bt.setMargin(new Insets(0, 0, 0, 0));
		bt.setBorder(BorderFactory.createEmptyBorder());
		bt.setContentAreaFilled(false);

		rm.setOpaque(false);
		rm.setMargin(new Insets(0, 0, 0, 0));
		rm.setBorder(BorderFactory.createEmptyBorder());
		rm.setContentAreaFilled(false);

		add("Center", tfe);

		JPanel controls = PanelUtils.totalCenterInPanel(PanelUtils.gridElements(1, 2, 2, 0, bt, rm));
		controls.setOpaque(true);
		controls.setBackground(Theme.current().getBackgroundColor());
		controls.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 3, getBackground()));

		add("East", controls);
	}

	@Override public void setEnabled(boolean enabled) {
		tfe.setEnabled(enabled);
		bt.setEnabled(enabled);
		rm.setEnabled(enabled && !tfe.getText().isBlank());
	}

	public VTextField getVTextField() {
		return tfe;
	}

	public Sound getSound() {
		return new Sound(mcreator.getWorkspace(), tfe.getText());
	}

	public void addSoundSelectedListener(ActionListener a) {
		listeners.add(a);
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

		rm.setEnabled(sound != null && !sound.isEmpty());

		listeners.forEach(l -> l.actionPerformed(new ActionEvent("", 0, "")));
	}

	public void setText(String text) {
		this.setSound(text);
	}

}
