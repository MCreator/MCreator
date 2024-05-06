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
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class JJigsawPoolsList extends JSingleEntriesList<JJigsawPool, Structure.JigsawPool> {
	private final ModElement modElement;

	public JJigsawPoolsList(MCreator mcreator, IHelpContext gui, ModElement element) {
		super(mcreator, gui);
		this.modElement = element;

		entries.setLayout(new BoxLayout(entries, BoxLayout.PAGE_AXIS));

		add.setText(L10N.t("elementgui.structuregen.jigsaw_add_pool"));
		add.addActionListener(e -> {
			JJigsawPool pool = new JJigsawPool(this, gui, entries, entryList);
			registerEntryUI(pool);
			pool.addInitialEntry();
		});

		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
						BorderFactory.createEmptyBorder(2, 2, 2, 2))));
	}

	Validator newPoolNameValidator(VTextField nameField) {
		return new UniqueNameValidator(L10N.t("elementgui.structuregen.jigsaw_pool_name_validator"), nameField::getText,
				() -> entryList.stream().map(JJigsawPool::getPoolName),
				new RegistryNameValidator(nameField, L10N.t("elementgui.structuregen.jigsaw_pool_name_validator")));
	}

	ModElement getModElement() {
		return modElement;
	}

	@Override public void reloadDataLists() {
		entryList.forEach(JJigsawPool::reloadDataLists);
	}

	@Override public List<Structure.JigsawPool> getEntries() {
		return entryList.stream().map(JJigsawPool::getPool).filter(Objects::nonNull).toList();
	}

	@Override public void setEntries(List<Structure.JigsawPool> jigsawPools) {
		jigsawPools.forEach(e -> {
			JJigsawPool pool = new JJigsawPool(this, gui, entries, entryList);
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
