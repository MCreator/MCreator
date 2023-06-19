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

package net.mcreator.ui.minecraft.modellayers;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.types.LivingEntity;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.VariableTypeLoader;
import net.mcreator.workspace.resources.Model;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JModelLayerListEntry extends JPanel implements IValidable {
	private final Model default_model = new Model.BuiltInModel("Default");
	private final JComboBox<Model> model = new JComboBox<>(new Model[] { default_model });
	private final VComboBox<String> texture = new SearchableComboBox<>();
	private final JCheckBox glow = L10N.checkbox("elementgui.living_entity.layer_should_glow");
	private ProcedureSelector condition;
	private final MCreator mcreator;

	public JModelLayerListEntry(MCreator mcreator, IHelpContext gui, JPanel parent, List<JModelLayerListEntry> entryList) {
		super(new FlowLayout(FlowLayout.LEFT));
		this.mcreator = mcreator;

		condition = new ProcedureSelector(gui.withEntry("entity/condition_display_model_layer"), mcreator,
				L10N.t("elementgui.living_entity.layer_display_condition"), ProcedureSelector.Side.CLIENT, true,
				VariableTypeLoader.BuiltInTypes.LOGIC, Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity")).makeInline();

		reloadDataLists();

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		entryList.add(this);

		model.setRenderer(new ModelComboBoxRenderer());

		add(L10N.label("elementgui.living_entity.layer_model"));
		add(model);

		texture.setRenderer(new WTextureComboBoxRenderer.TypeTextures(mcreator.getWorkspace(), TextureType.ENTITY));
		texture.setValidator(() -> {
			if (texture.getSelectedItem() == null || texture.getSelectedItem().equals(""))
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR, L10N.t("elementgui.living_entity.layer_needs_texture"));
			return Validator.ValidationResult.PASSED;
		});

		add(L10N.label("elementgui.living_entity.layer_texture"));
		add(texture);

		add(glow);

		add(condition);

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.living_entity.remove_model_layer"));
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});
		add(remove);

		parent.revalidate();
		parent.repaint();
	}

	public void reloadDataLists() {
		condition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(model, ListUtils.merge(Collections.singletonList(default_model),
				Model.getModels(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(texture, ListUtils.merge(Collections.singleton(""),
				mcreator.getFolderManager().getTexturesList(TextureType.ENTITY).stream().map(File::getName)
						.collect(Collectors.toList())), "");
	}

	public LivingEntity.ModelLayerEntry getEntry() {
		LivingEntity.ModelLayerEntry entry = new LivingEntity.ModelLayerEntry();
		entry.model = ((Model)Objects.requireNonNull(model.getSelectedItem())).getReadableName();
		entry.texture = texture.getSelectedItem();
		entry.glow = glow.isSelected();
		entry.condition = condition.getSelectedProcedure();
		return entry;
	}

	public void setEntry(LivingEntity.ModelLayerEntry e) {
		if (e.model != null && !e.model.isEmpty())
			model.setSelectedItem(Model.getModelByParams(mcreator.getWorkspace(), e.model, e.model != "Default" ? Model.Type.JAVA : Model.Type.BUILTIN));
		texture.setSelectedItem(e.texture);
		glow.setSelected(e.glow);
		condition.setSelectedProcedure(e.condition);
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		return texture.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {

	}

	@Override public Validator getValidator() {
		return texture.getValidator();
	}
}
