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
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JJigsawPart extends JPanel implements IValidable {

	private final MCreator mcreator;
	private final JButton remove = new JButton(UIRES.get("16px.clear"));

	private final JSpinner weight = new JSpinner(new SpinnerNumberModel(1, 1, 150, 1));
	private final SearchableComboBox<String> structureSelector = new SearchableComboBox<>();
	private final JComboBox<String> projection = new JComboBox<>(new String[] { "rigid", "terrain_matching" });
	private final MCItemListField ignoreBlocks;

	public JJigsawPart(MCreator mcreator, JPanel parent, List<JJigsawPart> entryList) {
		super(new BorderLayout());
		this.mcreator = mcreator;

		setBackground(Theme.current().getAltBackgroundColor().darker());

		ignoreBlocks = new MCItemListField(mcreator, ElementUtil::loadBlocks);
		ignoreBlocks.setPreferredSize(new Dimension(260, 30));

		structureSelector.setValidator(() -> {
			if (structureSelector.getSelectedItem() == null || structureSelector.getSelectedItem().isEmpty())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.structuregen.error_select_structure_spawn"));
			return Validator.ValidationResult.PASSED;
		});
		ComponentUtils.deriveFont(structureSelector, 16);
		reloadDataLists();

		JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		line.setOpaque(false);

		line.add(L10N.label("elementgui.structuregen.jigsaw_entry_weight"));
		line.add(weight);

		line.add(L10N.label("elementgui.structuregen.jigsaw_entry_structure"));
		line.add(structureSelector);

		line.add(L10N.label("elementgui.structuregen.projection"));
		line.add(projection);

		line.add(L10N.label("elementgui.structuregen.ignore_blocks"));
		line.add(ignoreBlocks);

		final JComponent container = PanelUtils.expandHorizontally(this);

		entryList.add(this);
		parent.add(container);

		remove.setText(L10N.t("simple_list_entry.remove"));
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});

		add("West", line);
		add("East", PanelUtils.join(FlowLayout.CENTER, 0, 0, remove));

		setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));

		parent.revalidate();
		parent.repaint();
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		remove.setEnabled(enabled);
		weight.setEnabled(enabled);
		structureSelector.setEnabled(enabled);
		projection.setEnabled(enabled);
		ignoreBlocks.setEnabled(enabled);
	}

	public void reloadDataLists() {
		ComboBoxUtil.updateComboBoxContents(structureSelector, mcreator.getFolderManager().getStructureList());
	}

	public Structure.JigsawPool.JigsawPart getEntry() {
		Structure.JigsawPool.JigsawPart part = new Structure.JigsawPool.JigsawPart();
		part.weight = (int) weight.getValue();
		part.structure = structureSelector.getSelectedItem();
		part.projection = (String) projection.getSelectedItem();
		part.ignoredBlocks = ignoreBlocks.getListElements();
		return part;
	}

	public void setEntry(Structure.JigsawPool.JigsawPart part) {
		weight.setValue(part.weight);
		structureSelector.setSelectedItem(part.structure);
		projection.setSelectedItem(part.projection);
		ignoreBlocks.setListElements(part.ignoredBlocks);
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		return structureSelector.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
	}

	@Override public Validator getValidator() {
		return structureSelector.getValidator();
	}

}
