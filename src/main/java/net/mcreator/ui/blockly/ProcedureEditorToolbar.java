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

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ExternalBlockLoader;
import net.mcreator.blockly.java.ProcedureTemplateIO;
import net.mcreator.io.TemplatesLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JScrollablePopupMenu;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.init.UIRES;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class ProcedureEditorToolbar extends TransparentToolBar {

	private static final Logger LOG = LogManager.getLogger(ProcedureEditorToolbar.class);

	private JScrollablePopupMenu results = new JScrollablePopupMenu();

	public ProcedureEditorToolbar(MCreator mcreator, BlocklyPanel blocklyPanel) {
		setBorder(null);

		BlocklyTemplateDropdown templateDropdown = new BlocklyTemplateDropdown(blocklyPanel,
				TemplatesLoader.loadTemplates("ptpl", "ptpl"));

		JButton bs1 = new JButton("Template library");
		bs1.setPreferredSize(new Dimension(169, 16));
		bs1.setIcon(UIRES.get("18px.templatelib"));
		bs1.setOpaque(false);
		add(bs1);
		bs1.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				templateDropdown.show(e.getComponent(), e.getComponent().getWidth(), 0);
			}
		});
		ComponentUtils.normalizeButton5(bs1);

		JTextField search = new JTextField() {
			@Override public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(new Color(0.3f, 0.3f, 0.3f, 0.4f));
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
				g.setColor(new Color(0.4f, 0.4f, 0.4f, 0.3f));
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				g.setColor(Color.white);
				if (getText().equals("")) {
					g.setFont(g.getFont().deriveFont(11f));
					g.setColor(new Color(120, 120, 120));
					g.drawString("Search procedure blocks", 5, 18);
				}
			}
		};
		search.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) {
				super.focusLost(e);
				search.setText("");
				results.setFocusable(true);
			}
		});
		search.setPreferredSize(new Dimension(340, 22));

		search.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (!search.getText().equals("")) {
					String[] keyWords = search.getText().replaceAll("[^ a-zA-Z0-9/._-]+", "").split(" ");

					Set<ExternalBlockLoader.ToolboxBlock> filtered = new LinkedHashSet<>();

					for (ExternalBlockLoader.ToolboxBlock block : BlocklyLoader.INSTANCE.getProcedureBlockLoader()
							.getDefinedBlocks().values()) {
						if (block.getName().toLowerCase(Locale.ENGLISH)
								.contains(search.getText().toLowerCase(Locale.ENGLISH))) {
							filtered.add(block);
						}
					}

					for (ExternalBlockLoader.ToolboxBlock block : BlocklyLoader.INSTANCE.getProcedureBlockLoader()
							.getDefinedBlocks().values()) {
						for (String keyWord : keyWords) {
							if (block.getName().toLowerCase(Locale.ENGLISH)
									.contains(keyWord.toLowerCase(Locale.ENGLISH)) && (block.toolboxCategory != null
									&& block.toolboxCategory.getName().toLowerCase(Locale.ENGLISH)
									.contains(keyWord.toLowerCase(Locale.ENGLISH)))) {
								filtered.add(block);
								break;
							} else if (block.getName().toLowerCase(Locale.ENGLISH)
									.contains(keyWord.toLowerCase(Locale.ENGLISH))) {
								filtered.add(block);
								break;
							}
						}
					}

					if (!filtered.isEmpty()) {
						results.setVisible(false);

						results = new JScrollablePopupMenu();
						results.setMaximumVisibleRows(20);

						for (ExternalBlockLoader.ToolboxBlock block : filtered) {
							JMenuItem menuItem = new JMenuItem("<html>" + (block.toolboxCategory != null ?
									"<span style='background: #" + Integer
											.toHexString(block.toolboxCategory.getColor().getRGB()).substring(2)
											+ ";'>&nbsp;" + block.toolboxCategory.getName()
											+ "&nbsp;</span>&nbsp;&nbsp;" :
									"") + block.getName().replaceAll("%\\d+?",
									"&nbsp;<span style='background: #444444'>&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;"));
							menuItem.addActionListener(ev -> {
								if (block.toolboxXML != null) {
									blocklyPanel.addBlocksFromXML("<xml>" + block.toolboxXML + "</xml>");
								} else {
									blocklyPanel.addBlocksFromXML(
											"<xml><block type=\"" + block.machine_name + "\"></block></xml>");
								}
								blocklyPanel.requestFocus();
								results.setVisible(false);
							});

							results.add(menuItem);
						}

						results.setFocusable(false);
						results.show(search, 0, 23);
					} else {
						results.setVisible(false);
					}
				} else {
					results.setVisible(false);
				}
			}
		});

		JComponent component = PanelUtils.join(FlowLayout.LEFT, 0, 0, search);
		component.setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		add(component);

		add(Box.createHorizontalGlue());

		JButton bs2 = new JButton("Export procedure");
		bs2.setIcon(UIRES.get("18px.export"));
		bs2.setOpaque(false);
		add(bs2);
		bs2.addActionListener(event -> {
			File exp = FileDialogs.getSaveDialog(mcreator, new String[] { ".ptpl" });
			if (exp != null) {
				try {
					ProcedureTemplateIO.exportProcedure(blocklyPanel.getXML(), exp);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mcreator, "<html><b>Failed to export the procedure!</b><br>"
									+ "Your procedure may be empty or corrupted.", "Export failed",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		ComponentUtils.normalizeButton4(bs2);
		bs2.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));

		JButton bs3 = new JButton("Import procedure");
		bs3.setIcon(UIRES.get("18px.import"));
		bs3.setOpaque(false);
		add(bs3);
		bs3.addActionListener(event -> {
			File imp = FileDialogs.getOpenDialog(mcreator, new String[] { ".ptpl" });
			if (imp != null) {
				try {
					blocklyPanel.addBlocksFromXML(ProcedureTemplateIO.importBlocklyXML(imp));
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mcreator, "<html><b>Failed to import the procedure!</b><br>"
									+ "The procedure file you are importing is invalid.", "Import failed",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		ComponentUtils.normalizeButton4(bs3);
		bs3.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
	}
}
