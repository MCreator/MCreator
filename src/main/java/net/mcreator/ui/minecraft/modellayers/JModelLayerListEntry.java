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
import net.mcreator.ui.component.util.ComboBoxFullWidthPopup;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.elements.VariableTypeLoader;
import net.mcreator.workspace.resources.Model;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JModelLayerListEntry extends JSimpleListEntry<LivingEntity.ModelLayerEntry> implements IValidable {

	private final MCreator mcreator;
	private final Supplier<List<PropertyDataWithValue<?>>> entityDataListProvider;

	private final Model default_model = new Model.BuiltInModel("Default");
	private final JComboBox<Model> model = new JComboBox<>(new Model[] { default_model });
	private final JCheckBox disableHurtOverlay = L10N.checkbox("elementgui.living_entity.layer_disable_hurt_overlay");
	private final JCheckBox glow = L10N.checkbox("elementgui.living_entity.layer_should_glow");
	private final JComboBox<String> conditionSource = new JComboBox<>();
	private final ProcedureSelector condition;
	private final TextureComboBox texture;

	public JModelLayerListEntry(MCreator mcreator, IHelpContext gui, JPanel parent,
			List<JModelLayerListEntry> entryList, Supplier<List<PropertyDataWithValue<?>>> entityDataListProvider) {
		super(parent, entryList);
		this.mcreator = mcreator;
		this.entityDataListProvider = entityDataListProvider;

		this.texture = new TextureComboBox(mcreator, TextureType.ENTITY).requireValue(
				"elementgui.living_entity.layer_needs_texture");

		this.texture.setPreferredSize(new Dimension(240, 36));

		line.setLayout(new BorderLayout(2, 2));
		line.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

		condition = new ProcedureSelector(gui.withEntry("entity/condition_display_model_layer"), mcreator,
				L10N.t("elementgui.living_entity.layer_display_condition"), ProcedureSelector.Side.CLIENT, true,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity")).makeInline();

		conditionSource.addPopupMenuListener(new ComboBoxFullWidthPopup() {
			@Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				reloadConditionSources();
				super.popupMenuWillBecomeVisible(e);
			}
		});
		conditionSource.addActionListener(
				_ -> condition.setEnabled(isEnabled() && conditionSource.getSelectedIndex() <= 0));
		conditionSource.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXX");

		model.setPrototypeDisplayValue(new Model.BuiltInModel("XXXXXXXXXXXXXXXXXXXXXXXX"));
		model.setRenderer(new ModelComboBoxRenderer());

		JPanel topLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		JPanel bottomLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

		topLine.setOpaque(false);
		bottomLine.setOpaque(false);

		topLine.add(L10N.label("elementgui.living_entity.layer_model"));
		topLine.add(model);

		topLine.add(L10N.label("elementgui.living_entity.layer_texture"));
		topLine.add(texture);

		disableHurtOverlay.setOpaque(false);
		glow.setOpaque(false);

		topLine.add(
				HelpUtils.wrapWithHelpButton(gui.withEntry("entity/model_layer_hurt_animation"), disableHurtOverlay));
		topLine.add(HelpUtils.wrapWithHelpButton(gui.withEntry("entity/glow_texture"), glow));

		bottomLine.add(HelpUtils.wrapWithHelpButton(gui.withEntry("entity/layer_condition_source"),
				L10N.label("elementgui.living_entity.layer_condition_source")));
		bottomLine.add(conditionSource);

		bottomLine.add(condition);

		line.add(topLine, BorderLayout.NORTH);
		line.add(bottomLine, BorderLayout.SOUTH);

		conditionSource.setPreferredSize(new Dimension(220, 36));
	}

	@Override public void reloadDataLists() {
		condition.refreshListKeepSelected(null);
		reloadConditionSources();

		ComboBoxUtil.updateComboBoxContents(model, ListUtils.merge(Collections.singletonList(default_model),
				Model.getModels(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		texture.reload();
	}

	private void reloadConditionSources() {
		ComboBoxUtil.updateComboBoxContents(conditionSource,
				ListUtils.merge(Collections.singleton(L10N.t("elementgui.living_entity.layer_condition_procedure")),
						logicEntityDataNames()));
	}

	private List<String> logicEntityDataNames() {
		return entityDataListProvider.get().stream().filter(e -> e.property() instanceof PropertyData.LogicType)
				.map(e -> e.property().getName()).toList();
	}

	void entityDataListChanged() {
		reloadConditionSources();
		if (conditionSource.getSelectedIndex() == -1) // selected entity data entry was removed
			conditionSource.setSelectedIndex(0);
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		model.setEnabled(enabled);
		texture.setEnabled(enabled);
		glow.setEnabled(enabled);
		conditionSource.setEnabled(enabled);
		condition.setEnabled(enabled && conditionSource.getSelectedIndex() <= 0);
	}

	@Override public LivingEntity.ModelLayerEntry getEntry() {
		LivingEntity.ModelLayerEntry entry = new LivingEntity.ModelLayerEntry();
		entry.setWorkspace(mcreator.getWorkspace());
		entry.model = ((Model) Objects.requireNonNull(model.getSelectedItem())).getReadableName();
		entry.texture = texture.getTextureName();
		entry.disableHurtOverlay = disableHurtOverlay.isSelected();
		entry.glow = glow.isSelected();
		entry.condition = condition.getSelectedProcedure();
		entry.syncedDataCondition =
				conditionSource.getSelectedIndex() > 0 ? (String) conditionSource.getSelectedItem() : null;
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
		if (e.syncedDataCondition != null)
			conditionSource.setSelectedItem(e.syncedDataCondition);
		else if (conditionSource.getItemCount() > 0)
			conditionSource.setSelectedIndex(0);
		condition.setEnabled(isEnabled() && conditionSource.getSelectedIndex() <= 0);
	}

	@Override public ValidationResult getValidationStatus() {
		return texture.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
	}

	@Override public Validator getValidator() {
		return texture.getValidator();
	}

}
