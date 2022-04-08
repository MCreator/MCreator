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
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("MagicConstant") public class AcceleratorsManager {

	private static final Logger LOG = LogManager.getLogger("Accelerator Manager");

	private static final File file = UserFolderManager.getFileFromUserFolder("accelerators.yaml");

	public static AcceleratorsManager INSTANCE;

	private final Map<String, String> CACHE;

	public AcceleratorsManager() {
		LOG.debug("Loading accelerators...");

		CACHE = new HashMap<>();

		try {
			YamlReader reader = new YamlReader(new FileReader(file));
			Map<?, ?> readValues = (Map<?, ?>) reader.read();
			readValues.forEach((s, s2) -> {
				if (s instanceof String && s2 instanceof String) // if false default value is used
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

	public static void initAccelerators() {
		INSTANCE = new AcceleratorsManager();
	}

	public void setInCache(Accelerator accelerator) {
		setInCache(accelerator.getID(), accelerator.getKeyStroke());
	}

	public void setInCache(String id, KeyStroke key) {
		CACHE.put(id, key.toString());
	}

	public void loadAccelerators(ActionRegistry actionRegistry, boolean save) {
		if (!CACHE.isEmpty()) {
			actionRegistry.getActions().stream().filter(a -> a instanceof BasicAction ba && ba.getAccelerator() != null)
					.map(a -> (BasicAction) a).forEach(action -> {
						try {
							action.getAccelerator().changeKey(
									KeyStroke.getKeyStroke(CACHE.get(action.getAccelerator().getID())));
						} catch (NullPointerException e) {
							// If a key is missing, we add it using its default value
							setInCache(action.getAccelerator());
							LOG.info(action.getAccelerator().getID()
									+ " did not have a saved value. Default value will be used.");
						}
					});
			if (save)
				saveValues();
		} else {
			resetAll(actionRegistry);
			saveValues();
		}

		if (save)
			applyToAllInstances(actionRegistry.getMCreator().getApplication());
		else
			applyAccelerators(actionRegistry);
	}

	private void applyToAllInstances(MCreatorApplication application) {
		application.getOpenMCreators().stream().map(mcreator -> mcreator.actionRegistry)
				.forEach(this::applyAccelerators);
	}

	private void applyAccelerators(ActionRegistry actionRegistry) {
		actionRegistry.getActions().stream().filter(a -> a instanceof BasicAction ba && ba.getAccelerator() != null)
				.forEach(a -> {
					BasicAction action = (BasicAction) a;
					if (action.getAccelerator() != null)
						action.setAccelerator(action.getAccelerator().getKeyStroke());
				});
	}

	public void resetAll(ActionRegistry actionRegistry) {
		actionRegistry.getActions().stream().filter(a -> a instanceof BasicAction ba && ba.getAccelerator() != null)
				.forEach(a -> {
					BasicAction action = (BasicAction) a;
					if (action.getAccelerator() != null)
						setInCache(action.getAccelerator().reset());
				});
		saveValues();
	}

	public void saveValues() {
		try {
			YamlWriter writer = new YamlWriter(new FileWriter(file));
			writer.write(CACHE);
			writer.close();
		} catch (IOException e) {
			LOG.error("Error when creating the accelerators file.", e);
		}
	}
}
