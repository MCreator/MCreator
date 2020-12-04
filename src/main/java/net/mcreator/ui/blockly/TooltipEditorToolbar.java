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

public class TooltipEditorToolbar extends TransparentToolBar {

	private static final Logger LOG = LogManager.getLogger(TooltipEditorToolbar.class);

	public TooltipEditorToolbar(MCreator mcreator, BlocklyPanel blocklyPanel) {
		setBorder(null);

		TooltipTemplateDropdown templateDropdown = new TooltipTemplateDropdown(blocklyPanel,
				TemplatesLoader.loadTemplates("tttpl", "tttpl"));

		JButton setupButton = L10N.button("blockly.templates.tt_setup");
		setupButton.setPreferredSize(new Dimension(155,16));
		setupButton.setIcon(UIRES.get("18px.templatelib"));
		setupButton.setOpaque(false);
		add(setupButton);
		setupButton.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				templateDropdown.show(e.getComponent(), e.getComponent().getWidth(), 0);
			}
		});
		ComponentUtils.normalizeButton5(setupButton);

		add(Box.createHorizontalGlue());

		JButton exportButton = L10N.button("blockly.templates.tt_setup.export");
		exportButton.setIcon(UIRES.get("18px.export"));
		exportButton.setOpaque(false);
		add(exportButton);
		exportButton.addActionListener(action -> {
			File exp = FileDialogs.getSaveDialog(mcreator, new String[] { ".tttpl" });
			if (exp != null) {
				try {
					ProcedureTemplateIO.exportTooltipSetup(blocklyPanel.getXML(), exp);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mcreator, L10N.t("blockly.templates.tt_setup.export_failed.message"),
							L10N.t("blockly.templates.tt_setup.export_failed.title"), JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		ComponentUtils.normalizeButton4(exportButton);
		exportButton.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));

		JButton importButton = L10N.button("blockly.templates.tt_setup.import");
		importButton.setIcon(UIRES.get("18px.import"));
		importButton.setOpaque(false);
		add(importButton);
		importButton.addActionListener(event -> {
			File imp = FileDialogs.getOpenDialog(mcreator, new String[] { ".tttpl" });
			if (imp != null) {
				try {
					blocklyPanel.addBlocksFromXML(ProcedureTemplateIO.importBlocklyXML(imp));
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mcreator, L10N.t("blockly.templates.tt_setup.import_failed.message"),
							L10N.t("blockly.templates.tt_setup.import_failed.title"), JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		ComponentUtils.normalizeButton4(importButton);
		importButton.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
	}
}
