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

package net.mcreator.preferences;

import net.mcreator.preferences.data.PreferencesData;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;

/**
 * <p>This class defines a section inside the {@link PreferencesDialog}. It groups all {@link PreferencesEntry} together both visually and innerly (when storing preferences).
 * For examples on how to create a new {@link PreferencesSection} and declare new {@link PreferencesEntry} inside the class, see built-in sections {@link net.mcreator.preferences.data}.</p>
 *
 * <p>Java plugins need to initialize their section using {@link net.mcreator.plugin.events.ApplicationLoadedEvent}.
 * Example:
 *
 * <pre>
 *     {@code
 *     public class MyPlugin extends JavaPlugin {
 *
 *         public MySection mySection;
 *
 *         public MyPlugin(Plugin plugin) {
 *             addListener(ApplicationLoadedEvent.class, event -> {
 *                 mySection = new MySection("myIdentifier");
 *             });
 *         }
 *     }
 *     }
 * </pre></p>
 */
public abstract class PreferencesSection {

	/**
	 * <p>A unique {@link String} acting like a mod's id for this system. If you use this method with one of the built-in section (see {@link PreferencesData}), this parameter can <b><u>NOT</u></b> be {@code "core"} as the system use it for all built-in entries.</p>
	 */
	private final String preferencesIdentifier;

	public PreferencesSection(String preferencesIdentifier) {
		this.preferencesIdentifier = preferencesIdentifier;
	}

	/**
	 * <p>This method allows adding a new {@link PreferencesEntry} to this {@link PreferencesSection}.
	 * Contrary to {@link PreferencesSection#addPluginEntry(String, PreferencesEntry)}, this method does not allow to specify a custom {@code identifier},
	 * meaning the system will use the one provided by this section ({@link PreferencesSection#preferencesIdentifier}).</p>
	 *
	 * <p>For Java plugins: Use this method <b><u>ONLY</u></b> when making a custom {@link PreferencesSection}.
	 * Do <b><u>NOT</u></b> use with a built-in section (see {@link PreferencesData}).
	 * Custom {@link PreferencesEntry} will be added to the {@code core} identifier group, leading to many problems for you and users.</p>
	 *
	 * @param entry The new {@link PreferencesEntry} to add to this section
	 * @return The provided {@link PreferencesEntry} to register
	 */
	public final <T, S extends PreferencesEntry<T>> S addEntry(S entry) {
		entry.setSection(this);
		PreferencesManager.register(preferencesIdentifier, entry);
		return entry;
	}

	/**
	 * <p>This method allows adding a new {@link PreferencesEntry} to the {@link PreferencesSection}.
	 * This method is designed so Java plugins can add a custom {@link PreferencesEntry} to an already existing {@link PreferencesSection} created by MCreator (see {@link PreferencesData}).
	 * While this method will work in all types of cases, {@link #addPluginEntry(String, PreferencesEntry)} can be safely used when using a custom {@link PreferencesSection}}.</p>
	 *
	 * <p>Example of a Java plugin:
	 * <pre>
	 * {@code
	 * public class MyPlugin extends JavaPlugin {
	 *
	 * 	public final BooleanEntry myEntry = new BooleanEntry("displayMCreator", true);
	 *
	 *	public MyPlugin(Plugin plugin) {
	 * 		addListener(ApplicationLoadedEvent.class, event -> {
	 * 			PreferencesManager.PREFERENCES.ui.addJavaEntry("myIdentifier", myEntry);
	 * 		});
	 * 	}
	 *
	 * }
	 * }</pre></p>
	 *
	 * @param pluginPreferencesIdentifier A unique {@link String} acting like a mod's id for this system. If you use this method with one of the built-in section (see {@link PreferencesData}), this parameter can <b><u>NOT</u></b> be {@code "core"} as the system use it for all built-in entries.
	 * @param entry The new {@link PreferencesEntry} to add to this section
	 * @return The provided {@link PreferencesEntry} to register
	 */
	public final <T, S extends PreferencesEntry<T>> S addPluginEntry(String pluginPreferencesIdentifier, S entry) {
		entry.setSection(this);
		PreferencesManager.register(pluginPreferencesIdentifier, entry);
		return entry;
	}

	/**
	 *
	 * @return True {@link PreferencesDialog} should display this {@link PreferencesSection}. Usually, this should always be true. This was implemented for {@link net.mcreator.preferences.data.HiddenSection}.
	 */
	public boolean isVisible() {
		return true;
	}

	/**
	 * @return The registry name of this specific section. It is used, among other things, for the localization's key of the {@link PreferencesEntry} inside this section.
	 */
	public abstract String getSectionKey();

}
