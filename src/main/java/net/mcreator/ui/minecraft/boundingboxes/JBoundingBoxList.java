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

package net.mcreator.ui.minecraft.boundingboxes;

import net.mcreator.element.types.interfaces.IBlockWithBoundingBox;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.JEntriesList;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JBoundingBoxList extends JEntriesList {
	private final List<JBoundingBoxEntry> boundingBoxList = new ArrayList<>();
	private final JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));

	public JBoundingBoxList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(), gui);
		setOpaque(false);

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		add.setText(L10N.t("elementgui.common.add_bounding_box"));
		topbar.add(add);
		add("North", topbar);

		entries.setOpaque(false);
		add("Center", new JScrollPane(PanelUtils.pullElementUp(entries)));

		add.addActionListener(e -> {
			JBoundingBoxEntry entry = new JBoundingBoxEntry(entries, boundingBoxList).setEntryEnabled(this.isEnabled());
			registerEntryUI(entry);
			firePropertyChange("boundingBoxChanged", false, true);
		});

		entries.addPropertyChangeListener("boundingBoxChanged",
				e -> firePropertyChange("boundingBoxChanged", false, true));

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.common.bounding_box_entries"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		setPreferredSize(new Dimension(getPreferredSize().width, (int) (mcreator.getSize().height * 0.6)));
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		add.setEnabled(enabled);
		boundingBoxList.forEach(e -> e.setEntryEnabled(enabled));
	}

	public List<IBlockWithBoundingBox.BoxEntry> getBoundingBoxes() {
		return boundingBoxList.stream().map(JBoundingBoxEntry::getEntry).filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	public void setBoundingBoxes(List<IBlockWithBoundingBox.BoxEntry> box) {
		boundingBoxList.clear(); // Fixes failing tests
		box.forEach(e -> {
			JBoundingBoxEntry entry = new JBoundingBoxEntry(entries, boundingBoxList).setEntryEnabled(isEnabled());
			registerEntryUI(entry);
			entry.setEntry(e);
		});
	}

	public boolean isFullCube() {
		return boundingBoxList.stream().anyMatch(JBoundingBoxEntry::isNotEmpty) && boundingBoxList.stream()
				.filter(JBoundingBoxEntry::isNotEmpty).allMatch(JBoundingBoxEntry::isFullCube);
	}
}
