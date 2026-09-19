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
import net.mcreator.ui.MCreator;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.TagElement;

import java.util.ArrayList;

public class TagEvent extends MCREvent {

	private final Workspace workspace;
	private final TagElement tagElement;
	private final ArrayList<TagElement.Entry> tagValue;

	/**
	 * <p>This event is never called. It only aims to group all events inside a single class.</p>
	 *
	 * @param workspace <p>The {@link Workspace} instance where the event was called.</p>
	 * @param tagElement <p>The {@link TagElement} that triggered the event.</p>
	 */
	public TagEvent(Workspace workspace, TagElement tagElement, ArrayList<TagElement.Entry> tagValue) {
		this.workspace = workspace;
		this.tagElement = tagElement;
		this.tagValue = tagValue;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public TagElement getTagElement() {
		return tagElement;
	}

	public ArrayList<TagElement.Entry> getTagValue() {
		return tagValue;
	}

	/**
	 * <p>This event is triggered when a new {@link TagElement} is created and added to the workspace.</p>
	 */
	public static class Added extends TagEvent {
		/**
		 * @param tagElement <p>The new tag added to the workspace.</p>
		 */
		public Added(Workspace workspace, TagElement tagElement, ArrayList<TagElement.Entry> tagValue) {
			super(workspace, tagElement, tagValue);
		}
	}

	/**
	 * <p>This event is triggered when a parameter of the {@link TagElement} is changed.</p>
	 */
	public static class Changed extends TagEvent {
		private final ArrayList<TagElement.Entry> oldTagValue;

		/**
		 *
		 * @param tagElement <p>The version of the tag after it was modified.</p>
		 * @param oldTagValue <p>The tag's values before it was modified.</p>
		 */
		public Changed(Workspace workspace, TagElement tagElement, ArrayList<TagElement.Entry> tagValue, ArrayList<TagElement.Entry> oldTagValue) {
			super(workspace, tagElement, tagValue);
			this.oldTagValue = oldTagValue;

			oldTagValue.forEach(e -> {
				System.out.println(e.name());
				System.out.println(e.isManaged());
				System.out.println(e.owner());
			});
		}

		public ArrayList<TagElement.Entry> getOldTagValue() {
			return oldTagValue;
		}
	}

	/**
	 * <p>This event is triggered right before the tag is removed from the workspace.</p>
	 */
	public static class Removed extends TagEvent {

		/**
		 * @param tagElement <p>The tag that is about to be removed.</p>
		 */
		public Removed(Workspace workspace, TagElement tagElement, ArrayList<TagElement.Entry> tagValue) {
			super(workspace, tagElement, tagValue);
		}
	}
}
