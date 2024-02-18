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

package net.mcreator.element.parts;

import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Mod element parameters represented by instances of classes implementing this interface gather some used data
 * from the configured workspace. In other words, these objects need workspace reference defined
 * before they can be used by workspace managers or code generators.
 */
public interface IWorkspaceDependent {

	Logger LOG = LogManager.getLogger(IWorkspaceDependent.class);

	void setWorkspace(@Nullable Workspace workspace);

	@Nullable Workspace getWorkspace();

	static void processWorkspaceDependentObjects(Object object, Consumer<IWorkspaceDependent> processor) {
		if (object == null)
			return;

		// Pass workspace if IWorkspaceDependent
		if (object instanceof IWorkspaceDependent iws)
			processor.accept(iws);

		// Then check if we can pass workspace to any of the children
		if (object instanceof Iterable<?> list) {
			for (Object element : list)
				processWorkspaceDependentObjects(element, processor);
		} else if (object instanceof Map<?, ?> map) {
			for (Object element : map.keySet())
				processWorkspaceDependentObjects(element, processor);
			for (Object element : map.values())
				processWorkspaceDependentObjects(element, processor);
		} else if (object.getClass().isArray()) {
			int length = Array.getLength(object);
			for (int i = 0; i < length; i++)
				processWorkspaceDependentObjects(Array.get(object, i), processor);
		} else if (object.getClass().getModule() == MCreator.class.getModule()
				|| PluginLoader.INSTANCE.getPluginModules().contains(object.getClass().getModule())) {
			for (Field field : object.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
					try {
						processWorkspaceDependentObjects(field.get(object), processor);
					} catch (Exception e) {
						LOG.warn("Failed to pass workspace to field " + field.getName() + " of object "
								+ object.getClass().getSimpleName());
					}
				}
			}
		}
	}

}
