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

package net.mcreator.plugin.events.workspace.element;

import net.mcreator.plugin.MCREvent;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.SoundElement;

public class SoundElementEvent extends MCREvent {

	private final Workspace workspace;
	private final SoundElement element;

	/**
	 * <p>This event is never called. It only aims to group all events inside a single class.</p>
	 *
	 * @param workspace <p>The {@link Workspace} instance where the event was called.</p>
	 * @param element   <p>The {@link SoundElement} that triggered the event.</p>
	 */
	public SoundElementEvent(Workspace workspace, SoundElement element) {
		this.workspace = workspace;
		this.element = element;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public SoundElement getSoundElement() {
		return element;
	}

	/**
	 * <p>This event is triggered when a new {@link SoundElement} is created and added to the workspace.</p>
	 */
	public static class Added extends SoundElementEvent {
		/**
		 * @param element <p>The new sound added to the workspace.</p>
		 */
		public Added(Workspace workspace, SoundElement element) {
			super(workspace, element);
		}
	}

	/**
	 * <p>This event is triggered when a parameter of the {@link SoundElement} is changed.</p>
	 */
	public static class Changed extends SoundElementEvent {
		private final SoundElement oldSoundElement;

		/**
		 *
		 * @param element         <p>The version of the sound element after it was modified.</p>
		 * @param oldSoundElement <p>The old sound element before it was modified.</p>
		 */
		public Changed(Workspace workspace, SoundElement element, SoundElement oldSoundElement) {
			super(workspace, element);
			this.oldSoundElement = oldSoundElement;
		}

		public SoundElement getOldSoundElement() {
			return oldSoundElement;
		}
	}

	/**
	 * <p>This event is triggered right before the sound is removed from the workspace.</p>
	 */
	public static class Removed extends net.mcreator.plugin.events.workspace.element.SoundElementEvent {

		/**
		 * @param element <p>The sound that is about to be removed.</p>
		 */
		public Removed(Workspace workspace, SoundElement element) {
			super(workspace, element);
		}
	}
}
