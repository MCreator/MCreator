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
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

public class ConverterUtils {

	private static final Logger LOG = LogManager.getLogger("Converter utils");

	public static void convertElementToDifferentType(IConverter converter, ModElement source,
			@Nullable GeneratableElement result) {
		if (result != null) {
			// Delete old files of the source ME as it will be converted to a different ME that will have different/own files
			Object oldFiles = source.getMetadata("files");
			if (oldFiles instanceof List<?> fileList)
				// filter by files in workspace so one can not create .mcreator file that would delete files on computer when opened
				fileList.stream().map(e -> new File(source.getWorkspace().getWorkspaceFolder(),
								e.toString().replace("/", File.separator)))
						.filter(source.getWorkspace().getFolderManager()::isFileInWorkspace).forEach(File::delete);
			source.getWorkspace().removeModElement(source);

			result.getModElement()
					.setParentFolder(FolderElement.findFolderByPath(source.getWorkspace(), source.getFolderPath()));
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

	public static String findSuitableModElementName(Workspace workspace, String desiredName) {
		if (!workspace.containsModElement(desiredName))
			return desiredName;
		int i = 1;
		while (workspace.containsModElement(desiredName + i))
			i++;
		return desiredName + i;
	}

}
