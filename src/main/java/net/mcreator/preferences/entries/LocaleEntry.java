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

package net.mcreator.preferences.entries;

import com.google.gson.JsonElement;
import net.mcreator.preferences.PreferencesEntry;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.image.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class LocaleEntry extends PreferencesEntry<Locale> {

	public LocaleEntry(String id, Locale value) {
		super(id, value);
	}

	@Override public JComponent getComponent(Window parent, Consumer<EventObject> fct) {
		List<Locale> locales = new ArrayList<>(L10N.getSupportedLocales());
		locales.sort((a, b) -> {
			int sa = L10N.getUITextsLocaleSupport(a) + L10N.getHelpTipsSupport(a);
			int sb = L10N.getUITextsLocaleSupport(b) + L10N.getHelpTipsSupport(b);
			if (sa == sb)
				return a.getDisplayName().compareTo(b.getDisplayName());

			return sb - sa;
		});
		JComboBox<Locale> box = new JComboBox<>(locales.toArray(new Locale[0]));
		box.setRenderer(new LocaleListRenderer());
		box.setSelectedItem(this.value);
		box.addActionListener(fct::accept);
		return box;
	}

	@Override public void setValueFromComponent(JComponent component) {
		this.value = (Locale) ((JComboBox<?>) component).getSelectedItem();
	}

	@Override public void setValueFromJsonElement(JsonElement object) {
		this.value = PreferencesManager.gson.fromJson(object, Locale.class);
	}

	@Override public JsonElement getSerializedValue() {
		return PreferencesManager.gson.toJsonTree(value, Locale.class);
	}

	private static class LocaleListRenderer extends JLabel implements ListCellRenderer<Locale> {

		private int uiTextsPercent = 0;
		private int helpTipsPercent = 0;

		@Override
		public Component getListCellRendererComponent(JList<? extends Locale> list, Locale value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setOpaque(isSelected);
			setBackground(Theme.current().getInterfaceAccentColor());
			setForeground(Color.white);
			setBorder(new EmptyBorder(0, 1, 0, 0));

			ComponentUtils.deriveFont(this, 12);
			setText(" " + value.getDisplayName(Locale.ROOT));

			uiTextsPercent = L10N.getUITextsLocaleSupport(value);
			helpTipsPercent = L10N.getHelpTipsSupport(value);

			try {
				String flagpath = "/flags/" + value.toString().split("_")[1].toUpperCase(Locale.ENGLISH) + ".png";
				@SuppressWarnings("ConstantConditions") BufferedImage image = ImageIO.read(
						getClass().getResourceAsStream(flagpath));
				setIcon(new ImageIcon(ImageUtils.crop(image, new Rectangle(1, 2, 14, 11))));
			} catch (Exception ignored) { // flag not found, ignore
			}

			return this;
		}

		@Override public Dimension getPreferredSize() {
			return new Dimension(super.getPreferredSize().width, super.getPreferredSize().height + 15);
		}

		@Override protected void paintComponent(Graphics gx) {
			Graphics2D g = (Graphics2D) gx;

			g.translate(0, -5);
			super.paintComponent(g);
			g.translate(0, 5);

			g.setColor(Color.lightGray);
			g.fillRect(0, getHeight() - 11, getWidth(), 11);

			g.setColor(Color.getHSBColor((float) (1 / 3d - ((100 - uiTextsPercent) / 3d / 100d)), 0.65f, 0.9f));
			g.fillRect(0, getHeight() - 11, (int) ((getWidth() / 2 - 2) * (uiTextsPercent / 100d)), 11);

			g.setColor(Color.getHSBColor((float) (1 / 3d - ((100 - helpTipsPercent) / 3d / 100d)), 0.65f, 0.9f));
			g.fillRect(getWidth() / 2 + 2, getHeight() - 11, (int) ((getWidth() / 2 - 2) * (helpTipsPercent / 100d)),
					11);

			g.setFont(getFont().deriveFont(9f));
			g.setColor(Color.darkGray);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g.drawString("Texts: " + uiTextsPercent + "%", 2, getHeight() - 2);
			g.drawString("Tips: " + helpTipsPercent + "%", getWidth() / 2 + 2 + 2, getHeight() - 2);
		}

	}

}
