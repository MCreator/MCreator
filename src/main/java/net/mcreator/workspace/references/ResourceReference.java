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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Used to mark fields storing names of resources used by mod element instance.
 * For textures, {@link TextureReference} should be used instead as it also declares the texture type.
 * <br>Regular Model and Sound references using Model or Sound object do not need this annotation additionally.
 *
 * @apiNote This annotation can also be added to fields of type {@link Collection}, custom object, etc.
 * with their values known to match or have fields/methods with matching values.
 */
@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME) public @interface ResourceReference {

	/**
	 * The type of resource that the marked field can contain a reference to.
	 */
	String value();

}
