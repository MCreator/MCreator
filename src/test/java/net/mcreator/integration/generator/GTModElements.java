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

/*
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining
 * it with JBoss Forge (or a modified version of that library), containing
 * parts covered by the terms of Eclipse Public License, the licensors of
 * this Program grant you additional permission to convey the resulting work.
 */

package net.mcreator.integration.generator;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.IWorkspaceDependent;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GTModElements {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		for (ModElementType<?> modElementType : TestWorkspaceDataProvider.getOrderedModElementTypesForTests(
				workspace.getGeneratorConfiguration(), false)) {
			List<GeneratableElement> modElementExamples = TestWorkspaceDataProvider.getModElementExamplesFor(workspace,
					modElementType, false, random);

			LOG.info("[{}] Testing mod element type generation {} with {} variants", generatorName,
					modElementType.getReadableName(), modElementExamples.size());

			for (GeneratableElement generatableElement : modElementExamples) {
				// Check if all workspace fields are not null (from the TestWorkspaceDataProvider)
				IWorkspaceDependent.processWorkspaceDependentObjects(generatableElement,
						workspaceDependent -> assertNotNull(workspaceDependent.getWorkspace()));

				ModElement modElement = generatableElement.getModElement();

				workspace.addModElement(modElement);

				assertTrue(workspace.getGenerator().generateElement(generatableElement));

				workspace.getModElementManager().storeModElement(generatableElement);
				workspace.getModElementManager().storeModElementPicture(generatableElement);

				// testing if element file deletion works properly (no exception thrown)
				workspace.getGenerator().removeElementFilesAndWorkspaceLinks(generatableElement);

				// generate back after removal for build testing
				assertTrue(workspace.getGenerator().generateElement(generatableElement));
			}
		}
	}

}
