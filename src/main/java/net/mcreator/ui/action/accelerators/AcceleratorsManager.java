/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.ui.action.accelerators;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import net.mcreator.io.UserFolderManager;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AcceleratorsManager {

	private static final Logger LOG = LogManager.getLogger("Accelerator Manager");

	private static final File file = UserFolderManager.getFileFromUserFolder("accelerators.yaml");

	public static AcceleratorsManager INSTANCE;

	/**
	 * <p>The cache is the {@link Map} where we take values when accelerators are loaded, and where we save into the file.
	 * Every changed accelerator is set in the cache only, so the user can cancel a change if he did not save yet.
	 * The cache is written every time the file is created for the first time and each time the user confirms his changes in {@link net.mcreator.ui.dialogs.AcceleratorDialog}.</p>
	 */
	private final Map<String, String> CACHE;

	public static void initAccelerators() {
		INSTANCE = new AcceleratorsManager();
	}

	/**
	 * <p>When this constructor is called, accelerators are loaded inside the CACHE map from the file.</p>
	 */
	public AcceleratorsManager() {
		LOG.debug("Loading accelerators...");

		CACHE = new HashMap<>();

		try {
			YamlReader reader = new YamlReader(new FileReader(file));
			((Map<?, ?>) reader.read()).forEach((s, s2) -> {
				if (s instanceof String && s2 instanceof String) // if false the default value will be used
					CACHE.put((String) s, (String) s2);
			});

			reader.close();
		} catch (IOException e) {
			LOG.info("Error when reading accelerators. Default values will be used.");
			try {
				file.createNewFile();
			} catch (IOException ex) {
				LOG.error("Error when creating the accelerators file.", ex);
			}
		}
	}

	/**
	 * <p>This method adds to the cache the provided accelerator. If the cache already contains the accelerator, the value will simply be changed.</p>
	 *
	 * @param accelerator <p>The {@link Accelerator} to add in the cache</p>
	 */
	public void setInCache(Accelerator accelerator) {
		setInCache(accelerator.getID(), accelerator.getKeyStroke());
	}

	/**
	 * <p>This method adds to the cache the provided {@link KeyStroke} to the ID given.
	 * If the cache already contains the ID, the value will simply be changed.</p>
	 *
	 * @param id  <p>This String is the key added in the cache.</p>
	 * @param key <p>This {@link KeyStroke} is the value to save.</p>
	 */
	public void setInCache(String id, KeyStroke key) {
		CACHE.put(id, key.toString());
	}

	/**
	 * <p>If the cache contains values, each {@link BasicAction} from the {@link ActionRegistry} are iterated to change their {@link Accelerator} for the value inside the cache.
	 * However, if the cache does not contain the accelerator, its default value is taken and added to the cache.
	 * In the case the cache is empty (e.g. the file was created during this launches), we set default values to all accelerators.
	 * Then, accelerators are applied to the actions.</p>
	 *
	 * @param actionRegistry <p>This is the {@link ActionRegistry} where we take the {@link BasicAction}s.</p>
	 */
	public void loadAccelerators(ActionRegistry actionRegistry) {
		if (!CACHE.isEmpty()) {
			actionRegistry.getActions().stream().filter(a -> a instanceof BasicAction ba && ba.getAccelerator() != null)
					.map(a -> (BasicAction) a).forEach(action -> {
						try {
							action.getAccelerator()
									.changeKey(KeyStroke.getKeyStroke(CACHE.get(action.getAccelerator().getID())));
						} catch (NullPointerException e) {
							// If a key is missing, we add it using its default value
							setInCache(action.getAccelerator());
							LOG.info(action.getAccelerator().getID()
									+ " did not have a saved value. Default value will be used.");
						}
					});
		} else {
			setDefaultValues(actionRegistry);
		}
		// We save values in case a new accelerator has been added
		saveValues();

		applyAccelerators(actionRegistry);
	}

	/**
	 * <p>Each {@link Accelerator} is applied to their {@link BasicAction}.</p>
	 *
	 * @param actionRegistry <p>This is the {@link ActionRegistry} where we take the {@link BasicAction}s.</p>
	 */
	private void applyAccelerators(ActionRegistry actionRegistry) {
		actionRegistry.getActions().stream().filter(a -> a instanceof BasicAction ba && ba.getAccelerator() != null)
				.forEach(a -> {
					BasicAction action = (BasicAction) a;
					if (action.getAccelerator() != null)
						action.setAccelerator(action.getAccelerator().getKeyStroke());
				});
	}

	/**
	 * <p>Set default values to all accelerators.</p>
	 *
	 * @param actionRegistry <p>This is the {@link ActionRegistry} where we take the {@link BasicAction}s.</p>
	 */
	public void setDefaultValues(ActionRegistry actionRegistry) {
		actionRegistry.getActions().stream().filter(a -> a instanceof BasicAction ba && ba.getAccelerator() != null)
				.forEach(a -> {
					BasicAction action = (BasicAction) a;
					if (action.getAccelerator() != null)
						setInCache(action.getAccelerator().reset());
				});
		saveValues();
	}

	/**
	 * <p>Write the cache inside the file.</p>
	 */
	public void saveValues() {
		try {
			if (!file.exists()) // Re-create the file if the users delete it while MCreator is opened.
				file.createNewFile();

			YamlWriter writer = new YamlWriter(new FileWriter(file));
			writer.write(CACHE);
			writer.close();
			LOG.debug("Accelerators have been successfully saved!");
		} catch (IOException e) {
			LOG.error("Error when creating the accelerators file.", e);
		}
	}
}
