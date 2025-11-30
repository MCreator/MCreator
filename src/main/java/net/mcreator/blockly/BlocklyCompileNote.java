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

package net.mcreator.blockly;

import net.mcreator.ui.validation.Validator;

public record BlocklyCompileNote(Type type, String message) implements Comparable<BlocklyCompileNote> {

	@Override public String toString() {
		return type.name() + ": " + message;
	}

	@Override public int compareTo(BlocklyCompileNote o) {
		if (this.type() == o.type()) {
			return 0;
		} else {
			return this.type().priority > o.type().priority ? -1 : 1;
		}
	}

	public Validator.ValidationResultType getValidationResultType() {
		return type.validationResultType;
	}

	public enum Type {
		INFO(0, Validator.ValidationResultType.PASSED),
		WARNING(1, Validator.ValidationResultType.WARNING),
		ERROR(2, Validator.ValidationResultType.ERROR);

		private final int priority;
		private final Validator.ValidationResultType validationResultType;

		Type(int priority, Validator.ValidationResultType validationResultType) {
			this.priority = priority;
			this.validationResultType = validationResultType;
		}
	}

}
