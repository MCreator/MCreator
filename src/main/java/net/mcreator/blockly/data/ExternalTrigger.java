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

import net.mcreator.ui.init.L10N;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

public class ExternalTrigger {

	String id;
	String name;

	@Nullable public List<String> required_apis;

	@Nullable public List<Dependency> dependencies_provided;

	// Used mostly by Java Edition triggers
	public boolean cancelable;
	public boolean has_result;
	public String side = "both";

	// Used mostly by Bedrock Edition triggers
	@Nullable public String type;

	public String getID() {
		return id;
	}

	public String getType() {
		if (type == null)
			return "global";
		return type;
	}

	public String getGroupEstimate() {
		// Try to remove commonly used namespaces for better estimation
		String idNoNamespace = this.id.replace("be_", "" );

		int a = StringUtils.ordinalIndexOf(idNoNamespace, "_", 2);
		if (a > 0)
			return idNoNamespace.substring(0, a);
		return idNoNamespace.split("_" )[0];
	}

	public String getName() {
		String l10nname = L10N.t("trigger." + id);
		if (l10nname != null)
			return l10nname;

		return name;
	}

}
