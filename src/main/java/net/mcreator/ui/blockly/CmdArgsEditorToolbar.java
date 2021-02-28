/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2021 Pylo and contributors
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

package net.mcreator.ui.blockly;

import net.mcreator.blockly.java.ProcedureTemplateIO;
import net.mcreator.io.TemplatesLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class CmdArgsEditorToolbar extends TransparentToolBar {

	private static final Logger LOG = LogManager.getLogger(CmdArgsEditorToolbar.class);

	public CmdArgsEditorToolbar(MCreator mcreator, BlocklyPanel blocklyPanel) {
		setBorder(null);

		BlocklyTemplateDropdown templateDropdown = new BlocklyTemplateDropdown(blocklyPanel,
				TemplatesLoader.loadTemplates("cmdtpl", "cmdtpl"));

		JButton bs1 = L10N.button("blockly.templates.cmd_setup");
		bs1.setPreferredSize(new Dimension(155, 16));
		bs1.setIcon(UIRES.get("18px.templatelib"));
		bs1.setOpaque(false);
		add(bs1);
		bs1.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				templateDropdown.show(e.getComponent(), e.getComponent().getWidth(), 0);
			}
		});
		ComponentUtils.normalizeButton5(bs1);

		add(Box.createHorizontalGlue());

		JButton bs2 = L10N.button("blockly.templates.cmd_setup.export");
		bs2.setIcon(UIRES.get("18px.export"));
		bs2.setOpaque(false);
		add(bs2);
		bs2.addActionListener(event -> {
			File exp = FileDialogs.getSaveDialog(mcreator, new String[] { ".cmdtpl" });
			if (exp != null) {
				try {
					ProcedureTemplateIO.exportCmdArgsSetup(blocklyPanel.getXML(), exp);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mcreator, L10N.t("blockly.templates.cmd_setup.export_failed.message"),
							L10N.t("blockly.templates.cmd_setup.export_failed.title"), JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		ComponentUtils.normalizeButton4(bs2);
		bs2.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));

		JButton bs3 = L10N.button("blockly.templates.cmd_setup.import");
		bs3.setIcon(UIRES.get("18px.import"));
		bs3.setOpaque(false);
		add(bs3);
		bs3.addActionListener(event -> {
			File imp = FileDialogs.getOpenDialog(mcreator, new String[] { ".cmdtpl" });
			if (imp != null) {
				try {
					blocklyPanel.addBlocksFromXML(ProcedureTemplateIO.importBlocklyXML(imp));
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mcreator, L10N.t("blockly.templates.cmd_setup.import_failed.message"),
							L10N.t("blockly.templates.cmd_setup.import_failed.title"), JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		ComponentUtils.normalizeButton4(bs3);
		bs3.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
	}
}
