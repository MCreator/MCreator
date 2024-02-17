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

package net.mcreator.workspace.references;

import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.generator.mapping.MappableElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Used to mark fields that can store names of custom elements (mappable or MEs) used by mod element instance
 * and NOT already included as names of objects like {@link MappableElement} or {@link Procedure}.
 * <br>Format of strings: with {@code CUSTOM:} prefix for entries created from other mod elements
 * or unmapped name for data list entries.
 *
 * @apiNote This annotation can also be added to fields of type {@link Collection}, custom object, etc.
 * with their values known to have fields/methods with matching values.
 */
@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME) public @interface ModElementReference {

	/**
	 * Special values indicating the element is not assigned.
	 */
	String[] defaultValues() default "";

}
