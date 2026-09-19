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

package net.mcreator.plugin.events.workspace.elements;

import net.mcreator.plugin.MCREvent;
import net.mcreator.ui.MCreator;
import net.mcreator.workspace.elements.VariableElement;

public class VariableEvent extends MCREvent {

	private final MCreator mcreator;
	private final VariableElement variable;

	/**
	 * <p>This event is never called. It only aims to group all events inside a single class.</p>
	 *
	 * @param mcreator <p>The {@link MCreator} instance where the event was called.</p>
	 * @param variable <p>The {@link VariableElement} that triggered the event.</p>
	 */
	public VariableEvent(MCreator mcreator, VariableElement variable) {
		this.mcreator = mcreator;
		this.variable = variable;
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	public VariableElement getVariable() {
		return variable;
	}

	public static class AddVariableEvent extends VariableEvent {

		/**
		 * <p>This event is triggered when a new {@link VariableElement} is created and added to the workspace.</p>
		 *
		 * @param variable <p>The new variable added to the workspace.</p>
		 */
		public AddVariableEvent(MCreator mcreator, VariableElement variable) {
			super(mcreator, variable);
		}
	}

	public static class ChangeVariableEvent extends VariableEvent {
		private final VariableElement oldVariable;

		/**
		 * <p>This event is triggered when a parameter of the {@link VariableElement} is changed.</p>
		 *
		 * @param variable <p>The updated version of the variable.</p>
		 * @param oldVariable <p>The variable before it was changed.</p>
		 */
		public ChangeVariableEvent(MCreator mcreator, VariableElement variable, VariableElement oldVariable) {
			super(mcreator, variable);
			this.oldVariable = oldVariable;
		}

		public VariableElement getOldVariable() {
			return oldVariable;
		}
	}

	public static class RemoveVariableEvent extends VariableEvent {

		/**
		 * <p>This event is triggered when a variable is deleted from the workspace.
		 * More precisely, the event is triggered right before the variable is actually removed from the workspace.</p>
		 *
		 * @param variable <p>The variable that is about to be removed.</p>
		 */
		public RemoveVariableEvent(MCreator mcreator, VariableElement variable) {
			super(mcreator, variable);
		}
	}
}
