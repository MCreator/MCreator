/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.gradle;

import org.gradle.tooling.BuildAction;
import org.gradle.tooling.BuildController;
import org.gradle.tooling.model.eclipse.EclipseProject;

import java.lang.reflect.Constructor;

public class GradleLoadEclipseModelAction implements BuildAction<EclipseProject> {

	private GradleLoadEclipseModelAction() {
	}

	@Override public EclipseProject execute(BuildController controller) {
		return controller.getModel(EclipseProject.class);
	}

	@SuppressWarnings("unchecked") public static GradleLoadEclipseModelAction loadFromIsolatedClassLoader() {
		try {
			Constructor<GradleLoadEclipseModelAction> constructor = (Constructor<GradleLoadEclipseModelAction>) IsolatedGradleClassLoader.loadClass(
					GradleLoadEclipseModelAction.class.getName()).getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
