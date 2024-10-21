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

package net.mcreator.ui.minecraft.entityanimations;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.Animation;
import net.mcreator.element.types.LivingEntity;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleListEntry;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class JEntityPlayableAnimationListEntry extends JSimpleListEntry<LivingEntity.PlayableAnimation>
		implements IValidable {

	private static final DataListEntry dummy = new DataListEntry.Dummy("No animation");

	private final MCreator mcreator;

	private final DataListComboBox animation;
	private final ProcedureSelector condition;

	public JEntityPlayableAnimationListEntry(MCreator mcreator, IHelpContext gui, JPanel parent,
			List<JEntityPlayableAnimationListEntry> entryList) {
		super(parent, entryList);
		this.mcreator = mcreator;

		line.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

		condition = new ProcedureSelector(gui.withEntry("entity/condition_animation"), mcreator,
				L10N.t("elementgui.living_entity.animation_condition"), ProcedureSelector.Side.CLIENT, true,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity")).makeInline()
				.setDefaultName(L10N.t("elementgui.living_entity.animation_external_trigger"));

		animation = new DataListComboBox(mcreator);

		animation.setValidator(() -> {
			if (animation.getSelectedItem() == dummy)
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.living_entity.select_animation_error"));
			return new Validator.ValidationResult(Validator.ValidationResultType.PASSED, "");
		});

		line.add(L10N.label("elementgui.living_entity.animation"));
		line.add(animation);

		line.add(condition);
	}

	@Override public void reloadDataLists() {
		condition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(animation,
				ListUtils.merge(Collections.singleton(dummy), ElementUtil.loadAnimations()), dummy);
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		animation.setEnabled(enabled);
		condition.setEnabled(enabled);
	}

	@Override public LivingEntity.PlayableAnimation getEntry() {
		LivingEntity.PlayableAnimation entry = new LivingEntity.PlayableAnimation();
		entry.animation = new Animation(mcreator.getWorkspace(), animation.getSelectedItem());
		entry.condition = condition.getSelectedProcedure();
		return entry;
	}

	@Override public void setEntry(LivingEntity.PlayableAnimation e) {
		animation.setSelectedItem(e.animation);
		condition.setSelectedProcedure(e.condition);
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		return animation.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
	}

	@Override public Validator getValidator() {
		return animation.getValidator();
	}

}
