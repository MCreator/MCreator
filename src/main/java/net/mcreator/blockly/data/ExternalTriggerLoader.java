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
import net.mcreator.blockly.Dependency;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.init.L10N;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

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

	public static class ExternalTrigger {

		private String id;
		private String name;

		@Nullable public List<String> required_apis;

		@Nullable public List<Dependency> dependencies_provided;

		public boolean cancelable;

		public boolean has_result;

		public String getID() {
			return id;
		}

		public String getGroupEstimate() {
			int a = StringUtils.ordinalIndexOf(this.id, "_", 2);
			if (a > 0)
				return this.id.substring(0, a);
			return this.id.split("_")[0];
		}

		public String getName() {
			String l10nname = L10N.t("trigger." + id);
			if (l10nname != null)
				return l10nname;

			return name;
		}

	}

}
