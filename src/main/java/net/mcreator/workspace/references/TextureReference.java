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

import net.mcreator.ui.workspace.resources.TextureType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Used to mark fields storing names of textures used by mod element instance.
 *
 * @apiNote This annotation can also be added to fields of type {@link Collection}, custom object, etc.
 * with their values known to match or have fields/methods with matching values.
 */
@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME) public @interface TextureReference {

	/**
	 * The type of texture that the marked field can contain a reference to.
	 */
	TextureType value();

	/**
	 * Template strings that the value of target type should be applied to in order to retrieve texture file names.
	 * An empty string means the value itself will be considered (effectively same as {@code "%s"}).
	 */
	String[] files() default "";

	/**
	 * Special values indicating the texture is not assigned.
	 */
	String[] defaultValues() default "";

}
