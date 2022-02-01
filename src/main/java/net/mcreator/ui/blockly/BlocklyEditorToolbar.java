/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.blockly.java.ProcedureTemplateIO;
import net.mcreator.io.TemplatesLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JScrollablePopupMenu;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.modgui.ProcedureGUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class BlocklyEditorToolbar extends TransparentToolBar {

	private static final Logger LOG = LogManager.getLogger(BlocklyEditorToolbar.class);

	private JScrollablePopupMenu results = new JScrollablePopupMenu();
	private final JButton templateLib;

	public BlocklyEditorToolbar(MCreator mcreator, String id, String extension, BlocklyPanel blocklyPanel) {
		this(mcreator, id, extension, blocklyPanel, null);
	}

	/**
	 * <p>A {@link BlocklyEditorToolbar} is the top panel added on every Java {@link BlocklyPanel}.
	 * It contains buttons like templates, an export and an import template buttons.</p>
	 *
	 * @param mcreator     <p>The {@link MCreator} instance used</p>
	 * @param id           <p>This String is mainly used for externalized texts.</p>
	 * @param extension    <p>This is the file extension of exported and imported templates.</p>
	 * @param blocklyPanel <p>The {@link BlocklyPanel} to use for some features</p>
	 * @param procedureGUI <p>When a {@link ProcedureGUI} is passed, features specific to {@link net.mcreator.element.types.Procedure} such as variables are enabled.</p>
	 */
	public BlocklyEditorToolbar(MCreator mcreator, String id, String extension, BlocklyPanel blocklyPanel,
			ProcedureGUI procedureGUI) {
		setBorder(null);

		BlocklyTemplateDropdown templateDropdown = new BlocklyTemplateDropdown(blocklyPanel,
				TemplatesLoader.loadTemplates(extension, extension), procedureGUI);

		templateLib = L10N.button("blockly.templates." + id);
		templateLib.setPreferredSize(new Dimension(155, 16));
		templateLib.setIcon(UIRES.get("18px.templatelib"));
		templateLib.setOpaque(false);
		add(templateLib);
		templateLib.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				templateDropdown.show(e.getComponent(), e.getComponent().getWidth(), 0);
			}
		});
		ComponentUtils.normalizeButton5(templateLib);

		if (procedureGUI != null) {
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
						g.drawString(L10N.t("blockly.search_procedure_blocks"), 5, 18);
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

						Set<ToolboxBlock> filtered = new LinkedHashSet<>();

						for (ToolboxBlock block : BlocklyLoader.INSTANCE.getProcedureBlockLoader().getDefinedBlocks()
								.values()) {
							if (block.getName().toLowerCase(Locale.ENGLISH)
									.contains(search.getText().toLowerCase(Locale.ENGLISH))) {
								filtered.add(block);
							}
						}

						for (ToolboxBlock block : BlocklyLoader.INSTANCE.getProcedureBlockLoader().getDefinedBlocks()
								.values()) {
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

							for (ToolboxBlock block : filtered) {
								JMenuItem menuItem = new JMenuItem("<html>" + (block.toolboxCategory != null ?
										"<span style='background: #" + Integer.toHexString(
												block.toolboxCategory.getColor().getRGB()).substring(2) + ";'>&nbsp;"
												+ block.toolboxCategory.getName() + "&nbsp;</span>&nbsp;&nbsp;" :
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

		}

		add(Box.createHorizontalGlue());

		JButton export = L10N.button("blockly.templates." + id + ".export");
		export.setIcon(UIRES.get("18px.export"));
		export.setOpaque(false);
		add(export);
		export.addActionListener(event -> {
			File exp = FileDialogs.getSaveDialog(mcreator, new String[] { "." + extension });
			if (exp != null) {
				try {
					ProcedureTemplateIO.exportAITaskSetup(blocklyPanel.getXML(), exp);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mcreator,
							L10N.t("blockly.templates." + id + ".export_failed.message"),
							L10N.t("blockly.templates." + id + ".export_failed.title"), JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		ComponentUtils.normalizeButton4(export);
		export.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));

		JButton import_ = L10N.button("blockly.templates." + id + ".import");
		import_.setIcon(UIRES.get("18px.import"));
		import_.setOpaque(false);
		add(import_);
		import_.addActionListener(event -> {
			File imp = FileDialogs.getOpenDialog(mcreator, new String[] { extension });
			if (imp != null) {
				try {
					blocklyPanel.addBlocksFromXML(ProcedureTemplateIO.importBlocklyXML(imp));
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mcreator,
							L10N.t("blockly.templates." + id + ".import_failed.message"),
							L10N.t("blockly.templates." + id + ".import_failed.title"), JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		ComponentUtils.normalizeButton4(import_);
		import_.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
	}

	public void setTemplateLibButtonWidth(int w) {
		templateLib.setPreferredSize(new Dimension(w, 16));
	}

}
