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
import net.mcreator.ui.component.entries.JSimpleListEntry;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.elements.VariableTypeLoader;
import net.mcreator.workspace.resources.Model;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JModelLayerListEntry extends JSimpleListEntry<LivingEntity.ModelLayerEntry> implements IValidable {

	private final MCreator mcreator;

	private final Model default_model = new Model.BuiltInModel("Default");
	private final JComboBox<Model> model = new JComboBox<>(new Model[] { default_model });
	private final JCheckBox disableHurtOverlay = L10N.checkbox("elementgui.living_entity.layer_disable_hurt_overlay");
	private final JCheckBox glow = L10N.checkbox("elementgui.living_entity.layer_should_glow");
	private final ProcedureSelector condition;
	private final TextureComboBox texture;

	public JModelLayerListEntry(MCreator mcreator, IHelpContext gui, JPanel parent,
			List<JModelLayerListEntry> entryList) {
		super(parent, entryList);
		this.mcreator = mcreator;

		this.texture = new TextureComboBox(mcreator, TextureType.ENTITY).requireValue(
				"elementgui.living_entity.layer_needs_texture");

		this.texture.setPreferredSize(new Dimension(240, 36));

		line.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

		condition = new ProcedureSelector(gui.withEntry("entity/condition_display_model_layer"), mcreator,
				L10N.t("elementgui.living_entity.layer_display_condition"), ProcedureSelector.Side.CLIENT, true,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity")).makeInline();

		model.setRenderer(new ModelComboBoxRenderer());

		line.add(L10N.label("elementgui.living_entity.layer_model"));
		line.add(model);

		line.add(L10N.label("elementgui.living_entity.layer_texture"));
		line.add(texture);

		disableHurtOverlay.setOpaque(false);
		glow.setOpaque(false);

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("entity/model_layer_hurt_animation"), disableHurtOverlay));
		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("entity/glow_texture"), glow));

		line.add(condition);
	}

	@Override public void reloadDataLists() {
		condition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(model, ListUtils.merge(Collections.singletonList(default_model),
				Model.getModels(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		texture.reload();
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		model.setEnabled(enabled);
		texture.setEnabled(enabled);
		glow.setEnabled(enabled);
		condition.setEnabled(enabled);
	}

	@Override public LivingEntity.ModelLayerEntry getEntry() {
		LivingEntity.ModelLayerEntry entry = new LivingEntity.ModelLayerEntry();
		entry.setWorkspace(mcreator.getWorkspace());
		entry.model = ((Model) Objects.requireNonNull(model.getSelectedItem())).getReadableName();
		entry.texture = texture.getTextureName();
		entry.disableHurtOverlay = disableHurtOverlay.isSelected();
		entry.glow = glow.isSelected();
		entry.condition = condition.getSelectedProcedure();
		return entry;
	}

	@Override public void setEntry(LivingEntity.ModelLayerEntry e) {
		if (e.model != null && !e.model.isEmpty()) {
			model.setSelectedItem(Model.getModelByParams(mcreator.getWorkspace(), e.model,
					e.model.equals("Default") ? Model.Type.BUILTIN : Model.Type.JAVA));
		}
		texture.setTextureFromTextureName(e.texture);
		disableHurtOverlay.setSelected(e.disableHurtOverlay);
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
