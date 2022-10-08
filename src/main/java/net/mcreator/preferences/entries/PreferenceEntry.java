/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import net.mcreator.ui.component.JColor;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * <p>This is the basic and the common class for all preferences of the software.
 * It stores everything the preference needs to have to be used or saved, such as an ID, its value or its {@link JComponent} for {@link PreferencesDialog}.
 * This is also the class used to save each preference inside the user's folder. However, only required (fields without transient) fields are saved.</p>
 * @param <T> <p>The type of the stored value.</p>
 */
public class PreferenceEntry<T> {

	private final String id;
	protected T value;
	private transient final String section;

	public PreferenceEntry(String id, T value, String section) {
		this.id = id;
		this.value = value;
		this.section = section;
	}

	/**
	 * <p>Generate a {@link JComponent} for the {@link PreferencesDialog}, so users can change the value.</p>
	 *
	 * @param parent <p>The component's parent, which is the opened {@link PreferencesDialog}.</p>
	 * @param fct <p>This is the {@link Consumer} used to enable the apply button when the value of the {@link JComponent} is changed.</p>
	 * @return <p>The {@link JComponent} to use inside the {@link PreferencesDialog} for all preference entries using the same type.</p>
	 */
	public JComponent getComponent(Window parent, Consumer<EventObject> fct) {
		if (value instanceof Boolean bool) {
			JCheckBox box = new JCheckBox();
			box.setSelected(bool);
			box.addActionListener(fct::accept);
			return box;
		} else if (value instanceof Color color) {
			JColor box = new JColor(parent, false, false);
			box.setColor(color);
			box.setColorSelectedListener(fct::accept);
			return box;
		} else if (value instanceof Locale locale) {
			List<Locale> locales = new ArrayList<>(L10N.getSupportedLocales());
			locales.sort((a, b) -> {
				int sa = L10N.getUITextsLocaleSupport(a) + L10N.getHelpTipsSupport(a);
				int sb = L10N.getUITextsLocaleSupport(b) + L10N.getHelpTipsSupport(b);
				if (sa == sb)
					return a.getDisplayName().compareTo(b.getDisplayName());

				return sb - sa;
			});
			JComboBox<Locale> box = new JComboBox<>(locales.toArray(new Locale[0]));
			box.setRenderer(new PreferencesDialog.LocaleListRenderer());
			box.setSelectedItem(locale);
			box.addActionListener(fct::accept);
			return box;
		}
		
		return null;
	}

	public String getID() {
		return id;
	}

	public T getValue() {
		return value;
	}

	public void setValue(Object object) {
		this.value = (T) object;
	}

	public String getSection() {
		return section;
	}

	@Override public String toString() {
		return id + ": value: " + value;
	}

}
