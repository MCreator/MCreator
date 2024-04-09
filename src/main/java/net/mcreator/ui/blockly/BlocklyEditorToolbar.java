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

import com.formdev.flatlaf.FlatClientProperties;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.blockly.data.ToolboxCategory;
import net.mcreator.blockly.java.BlocklyVariables;
import net.mcreator.blockly.java.ProcedureTemplateIO;
import net.mcreator.io.ResourcePointer;
import net.mcreator.io.TemplatesLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JScrollablePopupMenu;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.ProcedureGUI;
import net.mcreator.util.ColorUtils;
import net.mcreator.workspace.elements.VariableElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.*;

public class BlocklyEditorToolbar extends TransparentToolBar {

	private static final Logger LOG = LogManager.getLogger(BlocklyEditorToolbar.class);

	private JScrollablePopupMenu results = new JScrollablePopupMenu();
	private final JButton templateLib;

	private final BlocklyPanel blocklyPanel;

	private final JTextField search;

	public BlocklyEditorToolbar(MCreator mcreator, BlocklyEditorType blocklyEditorType, BlocklyPanel blocklyPanel) {
		this(mcreator, blocklyEditorType, blocklyPanel, null);
	}

	/**
	 * <p>A {@link BlocklyEditorToolbar} is the top panel added on every Java {@link BlocklyPanel}.
	 * It contains buttons like templates, an export and an import template buttons.</p>
	 *
	 * @param mcreator          <p>The {@link MCreator} instance used</p>
	 * @param blocklyEditorType <p>Type of the Blockly editor this toolbar will be used on.</p>
	 * @param blocklyPanel      <p>The {@link BlocklyPanel} to use for some features</p>
	 * @param procedureGUI      <p>When a {@link ProcedureGUI} is passed, features specific to {@link net.mcreator.element.types.Procedure} such as variables are enabled.</p>
	 */
	public BlocklyEditorToolbar(MCreator mcreator, BlocklyEditorType blocklyEditorType, BlocklyPanel blocklyPanel,
			ProcedureGUI procedureGUI) {
		this.blocklyPanel = blocklyPanel;

		setBorder(null);

		List<ResourcePointer> templates = TemplatesLoader.loadTemplates(blocklyEditorType.extension(),
				blocklyEditorType.extension());

		BlocklyTemplateDropdown templateDropdown = new BlocklyTemplateDropdown(blocklyPanel, templates, procedureGUI);

		templateLib = L10N.button("blockly.templates." + blocklyEditorType.registryName());
		templateLib.setPreferredSize(new Dimension(155, 16));
		templateLib.setIcon(UIRES.get("18px.templatelib"));
		templatesButtonStyle(templateLib);

		if (!templates.isEmpty())
			add(templateLib);

		templateLib.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				templateDropdown.show(e.getComponent(), e.getComponent().getWidth(), 0);
			}
		});

		search = new JTextField() {
			@Override public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (getText().isEmpty()) {
					g.setFont(g.getFont().deriveFont(11f));
					g.setColor(new Color(120, 120, 120));
					g.drawString(L10N.t("blockly.search_procedure_blocks"), 8, 18);
				}
			}
		};
		search.setBackground(ColorUtils.applyAlpha(search.getBackground(), 100));

		if (procedureGUI != null) {
			search.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
			search.addFocusListener(new FocusAdapter() {
				@Override public void focusLost(FocusEvent e) {
					super.focusLost(e);
					search.setText("");
					results.setFocusable(true);
				}
			});
			search.setPreferredSize(new Dimension(340, 22));

			search.getDocument().addDocumentListener(new DocumentListener() {
				@Override public void insertUpdate(DocumentEvent e) {
					updateSearch();
				}

				@Override public void removeUpdate(DocumentEvent e) {
					updateSearch();
				}

				@Override public void changedUpdate(DocumentEvent e) {
					updateSearch();
				}
			});

			JComponent component = PanelUtils.join(FlowLayout.LEFT, 0, 0, search);
			component.setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
			add(component);
		}

		add(Box.createHorizontalGlue());

		JButton export = L10N.button("blockly.templates." + blocklyEditorType.registryName() + ".export");
		export.setIcon(UIRES.get("18px.export"));
		add(export);
		export.addActionListener(event -> {
			File exp = FileDialogs.getSaveDialog(mcreator, new String[] { "." + blocklyEditorType.extension() });
			if (exp != null) {
				try {
					ProcedureTemplateIO.exportBlocklySetup(blocklyPanel.getXML(), exp, blocklyEditorType);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					JOptionPane.showMessageDialog(mcreator,
							L10N.t("blockly.templates." + blocklyEditorType.registryName() + ".export_failed.message"),
							L10N.t("blockly.templates." + blocklyEditorType.registryName() + ".export_failed.title"),
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		normalizeButton4(export);
		export.setForeground(Theme.current().getAltForegroundColor());

		JButton import_ = L10N.button("blockly.templates." + blocklyEditorType.registryName() + ".import");
		import_.setIcon(UIRES.get("18px.import"));
		add(import_);
		import_.addActionListener(event -> {
			File imp = FileDialogs.getOpenDialog(mcreator, new String[] { blocklyEditorType.extension() });
			if (imp != null) {
				// Run import in a separate thread to avoid blocking the UI and to avoid deadlocks on macOS
				new Thread(() -> {
					try {
						String procedureXml = ProcedureTemplateIO.importBlocklyXML(imp);
						if (procedureGUI != null) {
							Set<VariableElement> localVariables = BlocklyVariables.tryToExtractVariables(procedureXml);
							List<VariableElement> existingLocalVariables = blocklyPanel.getLocalVariablesList();

							for (VariableElement localVariable : localVariables) {
								if (existingLocalVariables.contains(localVariable))
									continue; // skip if variable with this name already exists

								blocklyPanel.addLocalVariable(localVariable.getName(),
										localVariable.getType().getBlocklyVariableType());
								procedureGUI.localVars.addElement(localVariable);
							}
						}
						blocklyPanel.addBlocksFromXML(procedureXml);
					} catch (Exception e) {
						LOG.error("Failed to import Blockly template", e);
						SwingUtilities.invokeLater(() -> {
							JOptionPane.showMessageDialog(mcreator,
									L10N.t("blockly.templates." + blocklyEditorType.registryName()
											+ ".import_failed.message"),
									L10N.t("blockly.templates." + blocklyEditorType.registryName()
											+ ".import_failed.title"), JOptionPane.WARNING_MESSAGE);
						});
					}
				}, "Blockly-Template-Import").start();
			}
		});
		normalizeButton4(import_);
		import_.setForeground(Theme.current().getAltForegroundColor());
	}

	private void updateSearch() {
		if (!search.getText().isEmpty()) {
			String[] keyWords = search.getText().replaceAll("[^ a-zA-Z0-9/._-]+", "").split(" ");

			Set<ToolboxBlock> filtered = new LinkedHashSet<>();

			for (ToolboxBlock block : BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.PROCEDURE)
					.getDefinedBlocks().values()) {
				if (block.getName().toLowerCase(Locale.ENGLISH)
						.contains(search.getText().toLowerCase(Locale.ENGLISH))) {
					filtered.add(block);
				}
			}

			for (ToolboxBlock block : BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.PROCEDURE)
					.getDefinedBlocks().values()) {
				for (String keyWord : keyWords) {
					if (block.getName().toLowerCase(Locale.ENGLISH).contains(keyWord.toLowerCase(Locale.ENGLISH)) && (
							block.getToolboxCategory() != null && block.getToolboxCategory().getName()
									.toLowerCase(Locale.ENGLISH).contains(keyWord.toLowerCase(Locale.ENGLISH)))) {
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
				results.setBackground(Theme.current().getBackgroundColor());
				results.setBorder(BorderFactory.createEmptyBorder());
				results.setMaximumVisibleRows(20);

				for (ToolboxBlock block : filtered) {
					JMenuItem menuItem = new JMenuItem(getHTMLForBlock(block));
					menuItem.addActionListener(ev -> {
						if (block.getToolboxXML() != null) {
							blocklyPanel.addBlocksFromXML("<xml>" + block.getToolboxXML() + "</xml>");
						} else {
							blocklyPanel.addBlocksFromXML(
									"<xml><block type=\"" + block.getMachineName() + "\"></block></xml>");
						}
						blocklyPanel.requestFocus();
						results.setVisible(false);
					});

					results.add(menuItem);
				}

				results.setFocusable(false);
				results.show(search, 0, 24);
			} else {
				results.setVisible(false);
			}
		} else {
			results.setVisible(false);
		}
	}

	public void setTemplateLibButtonWidth(int w) {
		templateLib.setPreferredSize(new Dimension(w, 16));
	}

	private String getHTMLForBlock(ToolboxBlock block) {
		List<ToolboxCategory> categories = new ArrayList<>();
		traverseCategories(categories, block.getToolboxCategory());

		StringBuilder builder = new StringBuilder("<html>");
		for (int i = categories.size() - 1; i >= 0; i--) {
			ToolboxCategory category = categories.get(i);
			builder.append("<span style='background: #")
					.append(Integer.toHexString(category.getColor().getRGB()).substring(2)).append(";'>&nbsp;")
					.append(category.getName()).append("&nbsp;</span>");
			if (i != 0)
				builder.append("<span style='background: #444444;'>&nbsp;&#x25B8;&nbsp;</span>");
		}
		builder.append("&nbsp;&nbsp;");
		builder.append(block.getName()
				.replaceAll("%\\d+?", "&nbsp;<span style='background: #444444'>&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;"));

		return builder.toString();
	}

	private void traverseCategories(List<ToolboxCategory> categories, ToolboxCategory category) {
		if (category != null) {
			categories.add(category);
			if (category.getParent() != null)
				traverseCategories(categories, category.getParent());
		}
	}

	private static void normalizeButton4(AbstractButton button) {
		button.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(0, 0, 0, 0)),
						BorderFactory.createCompoundBorder(
								BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1),
								BorderFactory.createMatteBorder(1, 3, 1, 3, new Color(0, 0, 0, 0)))));
		ComponentUtils.deriveFont(button, 11);
	}

	private static void templatesButtonStyle(AbstractButton button) {
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 1, 1, UIManager.getColor("Component.borderColor")),
				BorderFactory.createEmptyBorder(1, 0, 1, 0)));
		ComponentUtils.deriveFont(button, 11);
	}

}
