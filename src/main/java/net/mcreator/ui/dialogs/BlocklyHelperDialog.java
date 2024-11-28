/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.dialogs;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;

/**
 * This abstract class works as a base for dialogs that generate blocks for a {@link BlocklyPanel}.
 */
public abstract class BlocklyHelperDialog extends MCreatorDialog {

	public BlocklyHelperDialog(BlocklyPanel blocklyPanel, MCreator mcreator, String title, String okMessage,
			int width, int height) {
		super(mcreator, title, true);
		this.setLayout(new BorderLayout(10, 10));
		this.setIconImage(UIRES.get("18px.add").getImage());

		JButton ok = L10N.button(okMessage);
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		cancel.addActionListener(e -> dispose());
		ok.addActionListener(e -> {
			blocklyPanel.addBlocksFromXML(getXML());
			dispose();
		});
		this.add("South", PanelUtils.join(ok, cancel));

		this.setSize(width, height);
		this.getRootPane().setDefaultButton(ok);
		this.setLocationRelativeTo(mcreator);
	}

	/**
	 * @return The XML that will be converted to Blockly blocks.
	 */
	abstract String getXML();
}
