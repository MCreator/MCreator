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

package net.mcreator.plugin.events.ui;

import net.mcreator.plugin.MCREvent;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;

/**
 * <p>Events for different moments of {@link PreferencesDialog}.</p>
 */
public class PreferencesDialogEvent extends MCREvent {

	private final PreferencesDialog preferencesDialog;

	/**
	 * <p>This event is never called. It only aims to group all events of the dialog inside a single class.</p>
	 *
	 * @param preferencesDialog <p>The {@link PreferencesDialog} window that will be shown to the user.</p>
	 */
	protected PreferencesDialogEvent(PreferencesDialog preferencesDialog) {
		this.preferencesDialog = preferencesDialog;
	}

	public PreferencesDialog getPreferencesDialog() {
		return preferencesDialog;
	}

	/**
	 * <p>This event is called when different sections of the {@link PreferencesDialog} are being created and added.
	 * Template sections for {@link BlocklyEditorType} can be added using {@link PreferencesDialog#addEditTemplatesPanel(BlocklyEditorType)}.
	 * Custom template sections can also be added using {@link PreferencesDialog#addEditTemplatesPanel(String, String, String)}.
	 * Those new sections will be after all sections added by MCreator itself.</p>
	 */
	public static class SectionsLoaded extends PreferencesDialogEvent {

		public SectionsLoaded(PreferencesDialog preferencesDialog) {
			super(preferencesDialog);
		}
	}
}
