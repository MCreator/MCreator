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

package net.mcreator.element.converter;

import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ConverterUtils {

	private static final Logger LOG = LogManager.getLogger("Converter utils");

	public static void convertElementToDifferentType(IConverter converter, ModElement source,
			@Nullable GeneratableElement result) {
		if (result != null) {
			source.getWorkspace().removeModElement(source);

			result.getModElement().setParentFolder(FolderElement.dummyFromPath(source.getFolderPath()));
			source.getWorkspace().getModElementManager().storeModElementPicture(result);
			source.getWorkspace().addModElement(result.getModElement());
			source.getWorkspace().getGenerator().generateElement(result);
			source.getWorkspace().getModElementManager().storeModElement(result);

			LOG.debug("Converted mod element " + source.getName() + " (" + source.getTypeString() + ") to "
					+ result.getModElement().getType().getRegistryName() + " using " + converter.getClass()
					.getSimpleName());
		} else {
			source.getWorkspace().removeModElement(source);

			LOG.debug("Converted mod element " + source.getName() + " (" + source.getTypeString()
					+ ") to data format that is not a mod element using " + converter.getClass().getSimpleName());
		}
	}

}
