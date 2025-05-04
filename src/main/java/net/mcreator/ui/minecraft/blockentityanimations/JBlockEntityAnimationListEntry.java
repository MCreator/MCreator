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

package net.mcreator.ui.minecraft.blockentityanimations;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.Animation;
import net.mcreator.element.types.Block;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleListEntry;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JBlockEntityAnimationListEntry extends JSimpleListEntry<Block.AnimationEntry> {

	private final MCreator mcreator;

	private final DataListComboBox animation;
	private final ProcedureSelector condition;

	private final JSpinner speed = new JSpinner(new SpinnerNumberModel(1, 0, 100, 0.1));

	public JBlockEntityAnimationListEntry(MCreator mcreator, IHelpContext gui, JPanel parent,
			List<JBlockEntityAnimationListEntry> entryList) {
		super(parent, entryList);
		this.mcreator = mcreator;

		line.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

		condition = new ProcedureSelector(gui.withEntry("animations/condition_animation"), mcreator,
				L10N.t("elementgui.animations.animation_condition"), ProcedureSelector.Side.CLIENT, true,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate")).makeInline();

		animation = new DataListComboBox(mcreator);

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("animations/animation"),
				L10N.label("elementgui.animations.animation")));
		line.add(animation);

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("animations/animation_speed"),
				L10N.label("elementgui.animations.animation_speed")));
		line.add(speed);

		line.add(condition);

		speed.setPreferredSize(new Dimension(100, 36));
	}

	@Override public void reloadDataLists() {
		condition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(animation, ElementUtil.loadAnimations(mcreator.getWorkspace()));
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		animation.setEnabled(enabled);
		condition.setEnabled(enabled);
		speed.setEnabled(enabled);
	}

	@Override public Block.AnimationEntry getEntry() {
		Block.AnimationEntry entry = new Block.AnimationEntry();
		entry.animation = new Animation(mcreator.getWorkspace(), animation.getSelectedItem());
		entry.condition = condition.getSelectedProcedure();
		entry.speed = (double) speed.getValue();
		return entry;
	}

	@Override public void setEntry(Block.AnimationEntry e) {
		animation.setSelectedItem(e.animation);
		condition.setSelectedProcedure(e.condition);
		speed.setValue(e.speed);
	}

}
