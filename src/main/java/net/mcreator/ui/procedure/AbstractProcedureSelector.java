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
import com.google.gson.Strictness;
import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractProcedureSelector extends JPanel implements IValidable {

	private static final Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).create();

	final SearchableComboBox<ProcedureEntry> procedures = new SearchableComboBox<>();

	protected final Dependency[] providedDependencies;
	protected final Map<String, List<Dependency>> depsMap = new HashMap<>();
	protected final JLabel depslab = new JLabel();

	protected final JButton edit = new JButton(UIRES.get("18px.edit"));
	protected final JButton add = new JButton(UIRES.get("18px.add"));

	ProcedureEntry oldItem;

	protected final MCreator mcreator;

	protected final VariableType returnType;

	protected String defaultName = L10N.t("procedure.common.no_procedure");

	private boolean returnTypeOptional;

	public AbstractProcedureSelector(MCreator mcreator, @Nullable VariableType returnType,
			Dependency... providedDependencies) {
		this.mcreator = mcreator;
		this.returnType = returnType;

		this.providedDependencies = providedDependencies;

		setEnabled(isEnabled());
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		procedures.setEnabled(enabled);
		add.setEnabled(enabled && mcreator.getWorkspace().getGeneratorStats().getModElementTypeCoverageInfo()
				.get(ModElementType.PROCEDURE) != GeneratorStats.CoverageStatus.NONE);
		edit.setEnabled(enabled && getSelectedProcedure() != null);
	}

	public final void refreshList(@Nullable ReloadContext context) {
		if (context == null)
			context = ReloadContext.create(mcreator.getWorkspace());

		depsMap.clear();
		procedures.removeAllItems();

		procedures.addItem(new ProcedureEntry(defaultName, null));

		Set<Dependency> providedSet = new HashSet<>(Arrays.asList(providedDependencies));

		for (Map.Entry<ModElement, ReloadContext.ContexData> entry : context.data.entrySet()) {
			ModElement mod = entry.getKey();
			ReloadContext.ContexData data = entry.getValue();

			boolean missing = data.dependencies().stream().anyMatch(d -> !providedSet.contains(d));

			VariableType returnTypeCurrent = data.returnType();

			boolean correctReturnType = returnType == null || returnTypeCurrent == returnType;

			if (!missing)
				depsMap.put(mod.getName(), data.dependencies());

			if (correctReturnType || (returnTypeCurrent == null && returnTypeOptional))
				procedures.addItem(new ProcedureEntry(mod.getName(), returnTypeCurrent, !missing));
		}
	}

	public void refreshListKeepSelected(@Nullable ReloadContext context) {
		Procedure selected = getSelectedProcedure();
		refreshList(context);
		setSelectedProcedure(selected);
		updateDepsList(false);
	}

	ProcedureEntry updateDepsList(boolean smallIcons) {
		ProcedureEntry selected = procedures.getSelectedItem();

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
					.append(dependency.name()).append("&nbsp;</span><font size=1>&#32;</font>");

			idx++;
		}

		depslab.setText(deps.toString());

		edit.setEnabled(isEnabled() && getSelectedProcedure() != null);

		return selected;
	}

	public Procedure getSelectedProcedure() {
		ProcedureEntry selected = procedures.getSelectedItem();
		if (selected == null || selected.string.equals(defaultName))
			return null;
		return new Procedure(selected.string);
	}

	public void setSelectedProcedure(String procedure) {
		if (procedure != null)
			procedures.setSelectedItem(new ProcedureEntry(procedure, null));
	}

	public void setSelectedProcedure(Procedure procedure) {
		if (procedure != null)
			procedures.setSelectedItem(new ProcedureEntry(procedure.getName(), null));
	}

	public AbstractProcedureSelector makeReturnValueOptional() {
		returnTypeOptional = true;
		return this;
	}

	public void enableRealtimeValidation() {
		procedures.addActionListener(e -> getValidationStatus());
	}

	@Override public void paint(Graphics g) {
		super.paint(g);

		if (validator != null && currentValidationResult != null) {
			if (currentValidationResult.type() == ValidationResult.Type.WARNING) {
				g.setColor(currentValidationResult.type().getColor());
				g.drawRect(0, 0, getWidth(), getHeight());

				g.drawImage(UIRES.get("18px.warning").getImage(), getWidth() - 11, getHeight() - 11, 11, 11, null);
			} else if (currentValidationResult.type() == ValidationResult.Type.ERROR) {
				g.setColor(currentValidationResult.type().getColor());
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

				g.drawImage(UIRES.get("18px.remove").getImage(), 0, 0, 11, 11, null);
			}
		}
	}

	//validation code
	private Validator validator = null;
	private ValidationResult currentValidationResult = null;

	@Override public ValidationResult getValidationStatus() {
		ValidationResult validationResult = validator == null ? null : validator.validateIfEnabled(this);

		this.currentValidationResult = validationResult;

		//repaint as new validation status might have to be rendered
		repaint();

		return validationResult;
	}

	@Override public void setValidator(Validator validator) {
		this.validator = validator;
	}

	@Override public Validator getValidator() {
		return validator;
	}

	public enum Side {
		BOTH, CLIENT, SERVER
	}

	public static class ReloadContext {

		private final Map<ModElement, ContexData> data = new HashMap<>();

		public static ReloadContext create(Workspace workspace) {
			ReloadContext context = new ReloadContext();

			//noinspection FuseStreamOperations
			List<ModElement> procedureElements = workspace.getModElements().stream()
					.filter(mod -> mod.getType() == ModElementType.PROCEDURE).collect(Collectors.toList());
			procedureElements.sort(ModElement.getComparator(workspace, procedureElements));
			for (ModElement mod : procedureElements) {
				List<?> dependenciesList = (List<?>) mod.getMetadata("dependencies");
				if (dependenciesList != null) {
					List<Dependency> realdepsList = new ArrayList<>();
					for (Object depobj : dependenciesList) {
						Dependency dependency = gson.fromJson(gson.toJsonTree(depobj).getAsJsonObject(),
								Dependency.class);
						realdepsList.add(dependency);
					}

					VariableType returnTypeCurrent = mod.getMetadata("return_type") != null ?
							VariableTypeLoader.INSTANCE.fromName((String) mod.getMetadata("return_type")) :
							null;

					context.data.put(mod, new ContexData(realdepsList, returnTypeCurrent));
				}
			}
			return context;
		}

		record ContexData(List<Dependency> dependencies, @Nullable VariableType returnType) {}

	}

}
