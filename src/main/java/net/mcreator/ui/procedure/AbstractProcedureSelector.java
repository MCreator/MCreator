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

package net.mcreator.ui.procedure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.Procedure;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.*;

public abstract class AbstractProcedureSelector extends JPanel {

	private static final Gson gson = new GsonBuilder().setLenient().create();

	protected final SearchableComboBox<CBoxEntry> procedures = new SearchableComboBox<>();

	protected final Dependency[] providedDependencies;
	protected final Map<String, List<Dependency>> depsMap = new HashMap<>();
	protected final JLabel depslab = new JLabel();

	protected final JButton edit = new JButton(UIRES.get("18px.edit"));
	protected final JButton add = new JButton(UIRES.get("18px.add"));

	protected CBoxEntry oldItem;

	protected final MCreator mcreator;

	protected final VariableType returnType;

	protected String defaultName = L10N.t("procedure.common.no_procedure");

	private boolean returnTypeOptional;

	public AbstractProcedureSelector(MCreator mcreator, @Nullable VariableType returnType,
			Dependency... providedDependencies) {
		this.mcreator = mcreator;
		this.returnType = returnType;

		this.providedDependencies = providedDependencies;
	}

	@Override public void setEnabled(boolean enabled) {
		GeneratorConfiguration gc = mcreator.getGeneratorConfiguration();
		if (gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.PROCEDURE)
				== GeneratorStats.CoverageStatus.NONE)
			enabled = false;

		super.setEnabled(enabled);

		procedures.setEnabled(enabled);
		edit.setEnabled(enabled);
		add.setEnabled(enabled);
	}

	public void refreshList() {
		depsMap.clear();
		procedures.removeAllItems();

		procedures.addItem(new CBoxEntry(defaultName, null));

		for (ModElement mod : mcreator.getWorkspace().getModElements()) {
			if (mod.getType() == ModElementType.PROCEDURE) {
				List<?> dependenciesList = (List<?>) mod.getMetadata("dependencies");

				List<Dependency> realdepsList = new ArrayList<>();
				if (dependenciesList == null)
					continue;

				boolean missing = false;

				for (Object depobj : dependenciesList) {
					Dependency dependency = gson.fromJson(gson.toJsonTree(depobj).getAsJsonObject(), Dependency.class);
					realdepsList.add(dependency);
					if (!Arrays.asList(providedDependencies).contains(dependency))
						missing = true;
				}

				VariableType returnTypeCurrent = mod.getMetadata("return_type") != null ?
						VariableTypeLoader.INSTANCE.fromName((String) mod.getMetadata("return_type")) :
						null;

				boolean correctReturnType = true;
				if (returnType != null) {
					if (returnTypeCurrent != returnType)
						correctReturnType = false;
				}

				if (!missing)
					depsMap.put(mod.getName(), realdepsList);

				if (correctReturnType || (returnTypeCurrent == null && returnTypeOptional))
					procedures.addItem(new CBoxEntry(mod.getName(), returnTypeCurrent, !missing));
			}
		}
	}

	public void refreshListKeepSelected() {
		Procedure selected = getSelectedProcedure();
		refreshList();
		setSelectedProcedure(selected);
		updateDepsList(false);
	}

	protected CBoxEntry updateDepsList(boolean smallIcons) {
		CBoxEntry selected = procedures.getSelectedItem();

		List<Dependency> dependencies = null;
		if (selected != null) {
			dependencies = depsMap.get(selected.string);
		}

		StringBuilder deps = new StringBuilder();

		if (smallIcons)
			deps.append(
					"<html><div style='font-size: 8px; margin-top: 0; margin-bottom: 0; color: white; line-height: 20px;'>");
		else
			deps.append(
					"<html><div style='font-size: 9px; margin-top: 2px; margin-bottom: 1px; color: white; line-height: 20px;'>");

		int idx = 0;
		for (Dependency dependency : providedDependencies) {
			if (idx == 6 && providedDependencies.length > 6)
				deps.append("<p style='margin-top: 3'>");

			String bg = "999999";
			String optcss;

			if (dependencies != null && dependencies.contains(dependency)) {
				optcss = "color: #ffffff;";
				bg = Integer.toHexString(dependency.getColor().getRGB()).substring(2);
			} else {
				optcss = "color: #" + Integer.toHexString(dependency.getColor().darker().getRGB()).substring(2) + ";";
			}

			deps.append("<span style='background: #").append(bg).append("; ").append(optcss).append("'>&nbsp;")
					.append(dependency.getName()).append("&nbsp;</span>&#32;");

			idx++;
		}

		depslab.setText(deps.toString());
		edit.setEnabled(getSelectedProcedure() != null);

		if (selected == null || !selected.correctDependencies) {
			edit.setEnabled(false);
		}

		return selected;
	}

	public Procedure getSelectedProcedure() {
		CBoxEntry selected = procedures.getSelectedItem();
		if (selected == null || selected.string.equals(defaultName))
			return null;
		return new Procedure(selected.string);
	}

	public void setSelectedProcedure(String procedure) {
		if (procedure != null)
			procedures.setSelectedItem(new CBoxEntry(procedure, null));
	}

	public void setSelectedProcedure(Procedure procedure) {
		if (procedure != null)
			procedures.setSelectedItem(new CBoxEntry(procedure.getName(), null));
	}

	public AbstractProcedureSelector makeReturnValueOptional() {
		returnTypeOptional = true;
		return this;
	}

	public enum Side {
		BOTH, CLIENT, SERVER
	}

}
