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

package net.mcreator.ui.minecraft;

import net.mcreator.element.IBoundingBox;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JBoundingBoxEntry extends JPanel {
	private final JSpinner mx = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.1));
	private final JSpinner my = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.1));
	private final JSpinner mz = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.1));
	private final JSpinner Mx = new JSpinner(new SpinnerNumberModel(16, -100, 100, 0.1));
	private final JSpinner My = new JSpinner(new SpinnerNumberModel(16, -100, 100, 0.1));
	private final JSpinner Mz = new JSpinner(new SpinnerNumberModel(16, -100, 100, 0.1));
	private final JCheckBox subtract = new JCheckBox();
	private final JButton remove = new JButton(UIRES.get("16px.clear"));

	public JBoundingBoxEntry(JPanel parent, List<JBoundingBoxEntry> entryList) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		setBackground(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		entryList.add(this);

		JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT));
		line.setOpaque(false);

		line.add(L10N.label("elementgui.block.bounding_block_min_x"));
		line.add(mx);
		line.add(L10N.label("elementgui.block.bounding_block_min_y"));
		line.add(my);
		line.add(L10N.label("elementgui.block.bounding_block_min_z"));
		line.add(mz);

		line.add(new JEmptyBox(15, 5));

		line.add(L10N.label("elementgui.block.bounding_block_max_x"));
		line.add(Mx);
		line.add(L10N.label("elementgui.block.bounding_block_max_y"));
		line.add(My);
		line.add(L10N.label("elementgui.block.bounding_block_max_z"));
		line.add(Mz);

		line.add(new JEmptyBox(15, 5));

		line.add(L10N.label("elementgui.common.subtract"));
		line.add(subtract);
		subtract.setOpaque(false);

		remove.setText(L10N.t("elementgui.loot_table.remove_entry"));
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
			parent.firePropertyChange("boundingBoxChanged", false, true);
		});

		mx.addChangeListener(e -> parent.firePropertyChange("boundingBoxChanged", false, true));
		my.addChangeListener(e -> parent.firePropertyChange("boundingBoxChanged", false, true));
		mz.addChangeListener(e -> parent.firePropertyChange("boundingBoxChanged", false, true));
		Mx.addChangeListener(e -> parent.firePropertyChange("boundingBoxChanged", false, true));
		My.addChangeListener(e -> parent.firePropertyChange("boundingBoxChanged", false, true));
		Mz.addChangeListener(e -> parent.firePropertyChange("boundingBoxChanged", false, true));
		subtract.addActionListener(e -> parent.firePropertyChange("boundingBoxChanged", false, true));

		add(PanelUtils.centerAndEastElement(line, PanelUtils.join(remove)));

		parent.revalidate();
		parent.repaint();
	}

	public JBoundingBoxEntry setEntryEnabled(boolean enabled) {
		mx.setEnabled(enabled);
		my.setEnabled(enabled);
		mz.setEnabled(enabled);
		Mx.setEnabled(enabled);
		My.setEnabled(enabled);
		Mz.setEnabled(enabled);
		subtract.setEnabled(enabled);
		remove.setEnabled(enabled);

		return this;
	}

	public boolean isNotEmpty() {
		return this.getEntry().isNotEmpty();
	}

	public boolean isFullCube() {
		return this.getEntry().isFullCube();
	}

	public IBoundingBox.BoxEntry getEntry() {
		IBoundingBox.BoxEntry entry = new IBoundingBox.BoxEntry();
		entry.mx = (double) mx.getValue();
		entry.my = (double) my.getValue();
		entry.mz = (double) mz.getValue();
		entry.Mx = (double) Mx.getValue();
		entry.My = (double) My.getValue();
		entry.Mz = (double) Mz.getValue();
		entry.subtract = subtract.isSelected();

		return entry;
	}

	public void setEntry(IBoundingBox.BoxEntry box) {
		mx.setValue(box.mx);
		my.setValue(box.my);
		mz.setValue(box.mz);
		Mx.setValue(box.Mx);
		My.setValue(box.My);
		Mz.setValue(box.Mz);
		subtract.setSelected(box.subtract);
	}
}
