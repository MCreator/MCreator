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

package net.mcreator.blockly.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ExternalTriggerLoader {

	private final List<ExternalTrigger> externalTriggers;

	ExternalTriggerLoader(String resourceFolder) {
		this.externalTriggers = new ArrayList<>();

		final Gson gson = new GsonBuilder().setLenient().create();

		Set<String> fileNames = PluginLoader.INSTANCE.getResources(resourceFolder, Pattern.compile("^[^$].*\\.json"));
		for (String externalTriggerName : fileNames) {
			ExternalTrigger externalTrigger = gson
					.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, externalTriggerName),
							ExternalTrigger.class);
			externalTrigger.id = FilenameUtils.getBaseName(externalTriggerName);
			externalTriggers.add(externalTrigger);
		}

		externalTriggers
				.sort(Comparator.comparing(ExternalTrigger::getGroupEstimate).thenComparing(ExternalTrigger::getName));
	}

	public List<ExternalTrigger> getExternalTrigers() {
		return externalTriggers;
	}

}
