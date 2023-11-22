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

package net.mcreator.ui.minecraft.jigsaw;

import net.mcreator.element.types.Structure;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSingleEntriesList;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class JJigsawPoolsList extends JSingleEntriesList<JJigsawPool, Structure.JigsawPool> {

	public JJigsawPoolsList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, gui);

		entries.setLayout(new BoxLayout(entries, BoxLayout.PAGE_AXIS));

		add.setText(L10N.t("elementgui.structuregen.jigsaw_add_pool"));
		add.addActionListener(e -> {
			String name = VOptionPane.showInputDialog(mcreator,
					L10N.t("elementgui.structuregen.jigsaw_add_pool.message"),
					L10N.t("elementgui.structuregen.jigsaw_pool_name"), null, new OptionPaneValidatior() {
						@Override public ValidationResult validate(JComponent component) {
							RegistryNameValidator validator = new RegistryNameValidator((VTextField) component,
									L10N.t("elementgui.structuregen.jigsaw_pool_name"));
							return new UniqueNameValidator(L10N.t("elementgui.structuregen.jigsaw_pool_name"),
									((VTextField) component)::getText,
									() -> entryList.stream().map(JJigsawPool::getPoolName),
									validator).setIsPresentOnList(false).validate();
						}
					});
			if (name != null) {
				JJigsawPool pool = new JJigsawPool(this, gui, entries, entryList, name);
				registerEntryUI(pool);
				pool.addInitialEntry();
			}
		});

		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
						BorderFactory.createEmptyBorder(2, 2, 2, 2))));
	}

	@Override public void reloadDataLists() {
		entryList.forEach(JJigsawPool::reloadDataLists);
	}

	@Override public List<Structure.JigsawPool> getEntries() {
		return entryList.stream().map(JJigsawPool::getPool).filter(Objects::nonNull).toList();
	}

	@Override public void setEntries(List<Structure.JigsawPool> lootTablePools) {
		lootTablePools.forEach(e -> {
			JJigsawPool pool = new JJigsawPool(this, gui, entries, entryList, e.poolName);
			registerEntryUI(pool);
			pool.setPool(e);
		});
	}

	public AggregatedValidationResult getValidationResult() {
		AggregatedValidationResult validationResult = new AggregatedValidationResult();
		entryList.forEach(e -> validationResult.addValidationGroup(e.getValidationResult()));
		return validationResult;
	}

}
