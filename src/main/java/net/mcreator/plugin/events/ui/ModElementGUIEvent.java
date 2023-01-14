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

import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.modgui.ModElementGUI;

public class ModElementGUIEvent extends TabEvent {

	private final MCreator mcreator;
	private final ModElementGUI<?> modElementGUI;

	/**
	 * <p>These events are triggered at different states of a {@link ModElementGUI} loading.
	 * However, this constructor is never called outside this class.</p>
	 * @param mcreator <p>The {@link MCreator} instance where the mod element is.</p>
	 * @param tab <p>The {@link MCreatorTabs.Tab} that is opened for the {@link ModElementGUI}.</p>
	 * @param modElementGUI <p>The {@link ModElementGUI} that calls each event.</p>
	 */
	protected ModElementGUIEvent(MCreator mcreator, MCreatorTabs.Tab tab, ModElementGUI<?> modElementGUI) {
		super(tab);
		this.mcreator = mcreator;
		this.modElementGUI = modElementGUI;
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	public ModElementGUI<?> getModElementGUI() {
		return modElementGUI;
	}

	/**
	 * When a {@link ModElementGUI} is about to start loading, MCreator triggers this event.
	 * The event is triggered BEFORE any component or code is executed on the {@link net.mcreator.ui.MCreatorTabs.Tab}
	 * of the mod element.
	 */
	public static class BeforeLoading extends ModElementGUIEvent {
		public BeforeLoading(MCreator mcreator, MCreatorTabs.Tab tab, ModElementGUI<?> modElementGUI) {
			super(mcreator, tab, modElementGUI);
		}
	}

	/**
	 * When a {@link ModElementGUI} finishes to load, MCreator triggers this event.
	 * The event is triggered AFTER all components or code is executed on the {@link net.mcreator.ui.MCreatorTabs.Tab}
	 * of the mod element.
	 */
	public static class AfterLoading extends ModElementGUIEvent {
		public AfterLoading(MCreator mcreator, MCreatorTabs.Tab tab, ModElementGUI<?> modElementGUI) {
			super(mcreator, tab, modElementGUI);
		}
	}

	/**
	 * <p>When a {@link ModElementGUI} will be saved, MCreator triggers this event.
	 * The process of saving the mod element happens just after this event is executed.</p>
	 */
	public static class WhenSaving extends ModElementGUIEvent {
		/**
		 * <p>{@code True} when the user clicks on Save only. {@code False} when the mod element is saved and then, closed.</p>
		 */
		private final boolean savesOnly;
		public WhenSaving(MCreator mcreator, MCreatorTabs.Tab tab, ModElementGUI<?> modElementGUI, boolean savesOnly) {
			super(mcreator, tab, modElementGUI);
			this.savesOnly = savesOnly;
		}

		public boolean isSavedOnly() {
			return savesOnly;
		}
	}
}