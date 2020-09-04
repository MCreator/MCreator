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

package net.mcreator.ui.action.impl;

import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.VisitURIAction;
import net.mcreator.ui.init.L10N;

public class ShowDataListAction {

	public static class EntityIDs extends VisitURIAction {

		public EntityIDs(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.show_data_list.entity"),
					MCreatorApplication.SERVER_DOMAIN + "/wiki/entity-ids");
		}
	}

	public static class ItemBlockList extends VisitURIAction {

		public ItemBlockList(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.show_data_list.item_block"),
					MCreatorApplication.SERVER_DOMAIN + "/wiki/minecraft-block-and-item-list-registry-and-code-names");
		}
	}

	public static class ParticeIDList extends VisitURIAction {

		public ParticeIDList(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.show_data_list.particle"),
					MCreatorApplication.SERVER_DOMAIN + "/wiki/particles-ids");
		}
	}

	public static class SoundsList extends VisitURIAction {

		public SoundsList(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.show_data_list.sound"),
					MCreatorApplication.SERVER_DOMAIN + "/wiki/list-sound-effects-and-records");
		}
	}

	public static class FuelBurnTimes extends VisitURIAction {

		public FuelBurnTimes(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.show_data_list.burn_time"),
					MCreatorApplication.SERVER_DOMAIN + "/wiki/burn-time-fuels");
		}

	}

	public static class VanillaLootTables extends VisitURIAction {

		public VanillaLootTables(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.show_data_list.vanilla_loot_table"),
					MCreatorApplication.SERVER_DOMAIN + "/wiki/minecraft-vanilla-loot-tables-list");
		}

	}

}