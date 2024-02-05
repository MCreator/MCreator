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

package net.mcreator.ui.blockly;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.ui.validation.Validator;

import javax.annotation.Nullable;
import java.util.function.Function;

public class BlocklyValidationResult extends Validator.ValidationResult {

	public BlocklyValidationResult(BlocklyCompileNote note, @Nullable Function<String, String> messageFormatter) {
		super(toValidationResultType(note.type()),
				messageFormatter != null ? messageFormatter.apply(note.message()) : note.message());
	}

	private static Validator.ValidationResultType toValidationResultType(BlocklyCompileNote.Type type) {
		return switch (type) {
			case INFO -> Validator.ValidationResultType.PASSED;
			case WARNING -> Validator.ValidationResultType.WARNING;
			case ERROR -> Validator.ValidationResultType.ERROR;
		};
	}

}
