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

package net.mcreator.ui.minecraft.spawntrees;

import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.TreeEntry;
import net.mcreator.element.types.Biome;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JTreeListEntry extends JPanel {

	private final JComboBox treeType = new JComboBox<>();
	private final JSpinner count = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
	private final JSpinner extraChance = new JSpinner(new SpinnerNumberModel(0.1, 0, 1000, 0.1));
	private final JSpinner extraCount = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));

	private final JComboBox<String> shape = new JComboBox<>(new String[] {"ACACIA_TREE", "DARK_OAK_TREE", "FANCY_TREE",
			"JUNGLE_GROUND_BUSH", "MEGA_JUNGLE_TREE", "MEGA_SPRUCE_TREE", "NORMAL_TREE"});

	private final Workspace workspace;

	public JTreeListEntry(MCreator mcreator, JPanel parent, List<JTreeListEntry> entryList) {
		this.workspace = mcreator.getWorkspace();

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		entryList.add(this);

		ElementUtil.loadAllTrees(workspace).forEach(e -> treeType.addItem(e.getReadableName()));

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("biome/tree_config"),
				L10N.label("elementgui.biome.tree_config")));
		add(treeType);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("biome/tree_shape"),
				L10N.label("elementgui.biome.tree_shape")));
		add(shape);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("biome/trees_per_chunk"),
				L10N.label("elementgui.biome.trees_per_chunk")));
		add(count);

		add(HelpUtils
				.wrapWithHelpButton(IHelpContext.NONE.withEntry("biome/tree_extra_chance"),
						L10N.label("elementgui.biome.trees_extra_chance")));
		add(extraChance);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("biome/tree_extra_count"),
				L10N.label("elementgui.biome.trees_extra_count")));
		add(extraCount);

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.biome.remove_tree"));
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
	
	public Biome.SpawnTree getEntry() {
		Biome.SpawnTree entry = new Biome.SpawnTree();
		entry.tree = new TreeEntry(workspace, (String) treeType.getSelectedItem());
		entry.shape = (String) shape.getSelectedItem();
		entry.count = (int) count.getValue();
		entry.extraChance = (double) extraChance.getValue();
		entry.extraCount = (int) extraCount.getValue();
		return entry;
	}

	public void setEntry(Biome.SpawnTree e) {
		treeType.setSelectedItem(e.tree.getUnmappedValue());
		shape.setSelectedItem(e.shape);
		count.setValue(e.count);
		extraChance.setValue(e.extraChance);
		extraCount.setValue(e.extraCount);
	}
}
