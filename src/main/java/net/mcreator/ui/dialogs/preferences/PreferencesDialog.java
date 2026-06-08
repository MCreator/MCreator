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

package net.mcreator.ui.dialogs.preferences;

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.ui.PreferencesDialogEvent;
import net.mcreator.preferences.PreferencesEntry;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PreferencesDialog extends MCreatorDialog {

	final JPanel preferences = new JPanel();

	private final Map<String, JPanel> sectionPanels = new HashMap<>();

	private final DefaultMutableTreeNode sectionsRoot = new DefaultMutableTreeNode();
	private DefaultMutableTreeNode templatesNode;
	private final DefaultTreeModel sectionsModel = new DefaultTreeModel(sectionsRoot);
	private final JTree sections = new JTree(sectionsModel);
	private final CardLayout preferencesLayout = new CardLayout();

	private final Map<PreferencesEntry<?>, JComponent> entries = new HashMap<>();

	private final JButton apply = L10N.button("action.common.apply");

	private final Window parent;

	public PreferencesDialog(Window parent) {
		this(parent, null);
	}

	public PreferencesDialog(Window parent, @Nullable String selectedTab) {
		super(parent);

		this.parent = parent;

		setModal(true);
		setTitle(L10N.t("dialog.preferences.title_mcreator"));

		sections.setBackground(getBackground());
		sections.setRootVisible(false);
		sections.setShowsRootHandles(true);
		sections.setExpandsSelectedPaths(false);
		sections.setToggleClickCount(1);
		sections.setCellRenderer(new DefaultTreeCellRenderer() {
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				setBorder(new EmptyBorder(4, 8, 4, 10));
				return this;
			}
		});

		preferences.setLayout(preferencesLayout);

		JSplitPane spne = new JSplitPane();
		spne.setRightComponent(preferences);
		spne.setLeftComponent(new JScrollPane(sections));
		spne.setContinuousLayout(true);
		spne.setDividerLocation(185);
		add("Center", spne);

		sections.setBackground(Theme.current().getBackgroundColor());
		ComponentUtils.deriveFont(sections, 13);

		JButton ok = L10N.button("dialog.preferences.save");
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));

		JButton reset = L10N.button("dialog.preferences.restore_defaults");
		reset.addActionListener(_ -> {
			int option = JOptionPane.showConfirmDialog(null, L10N.t("dialog.preferences.restore_defaults_confirmation"),
					L10N.t("dialog.preferences.restore_defaults"), JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null);
			if (option == JOptionPane.YES_OPTION) {
				PreferencesManager.getPreferencesRegistry()
						.forEach((_, entries) -> PreferencesManager.resetFromList(entries));

				dispose();
				new PreferencesDialog(parent, getSelectedSection());
			}
		});

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(ok);
		buttons.add(cancel);
		buttons.add(apply);
		apply.setEnabled(false);

		JPanel buttonsleft = new JPanel();
		buttonsleft.add(reset);

		add("South", PanelUtils.westAndEastElement(buttonsleft, buttons));

		sections.addTreeSelectionListener(_ -> {
			String section = getSelectedSection();
			if (section != null)
				preferencesLayout.show(preferences, section);
		});

		loadSections();

		if (selectedTab != null)
			selectSection(selectedTab);

		ok.addActionListener(_ -> {
			savePreferences();
			dispose();
		});

		apply.addActionListener(_ -> {
			savePreferences();
			apply.setEnabled(false);
		});

		cancel.addActionListener(_ -> dispose());

		pack();
		setSize(Math.max(960, getBounds().width), 570);
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	private void loadSections() {
		// Add preference entries
		PreferencesManager.getPreferencesRegistry().forEach(
				(_, preferences) -> preferences.stream().filter(e -> e.getSection().isVisible()).toList()
						.forEach(entry -> {
							if (!sectionPanels.containsKey(entry.getSectionKey()))
								createPreferenceSection(entry.getSectionKey());
							entries.put(entry, generateEntryComponent(entry, sectionPanels.get(entry.getSectionKey())));
						}));

		new PluginsPanel(this);

		new ThemesPanel(this);

		templatesNode = new DefaultMutableTreeNode(L10N.t("dialog.preferences.templates"));
		sectionsRoot.add(templatesNode);

		addEditTemplatesPanel("ui_backgrounds", "backgrounds", "png");
		addEditTemplatesPanel("texture_templates", "templates/textures/texturemaker", "png");
		addEditTemplatesPanel("armor_templates", "templates/textures/armormaker", "png");

		BlocklyLoader.INSTANCE.getAllBlockLoaders().keySet().stream().filter(type -> type.extension() != null)
				.forEach(this::addEditTemplatesPanel);

		MCREvent.event(new PreferencesDialogEvent.SectionsLoaded(this));

		sections.collapsePath(new TreePath(templatesNode.getPath()));

		if (sectionsRoot.getChildCount() > 0) {
			DefaultMutableTreeNode first = (DefaultMutableTreeNode) sectionsRoot.getChildAt(0);
			sections.setSelectionPath(new TreePath(first.getPath()));
		}
	}

	void addSection(String name) {
		sectionsRoot.add(new DefaultMutableTreeNode(name));
		sectionsModel.reload();
	}

	void addTemplateSection(String name) {
		templatesNode.add(new DefaultMutableTreeNode(name));
		sectionsModel.reload();
	}

	@Nullable private String getSelectedSection() {
		TreePath path = sections.getSelectionPath();
		if (path == null)
			return null;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		if (!node.isLeaf())
			return null;
		return (String) node.getUserObject();
	}

	private void selectSection(String name) {
		for (int i = 0; i < sectionsRoot.getChildCount(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) sectionsRoot.getChildAt(i);
			if (node.isLeaf() && name.equals(node.getUserObject())) {
				sections.setSelectionPath(new TreePath(node.getPath()));
				return;
			}
			for (int j = 0; j < node.getChildCount(); j++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(j);
				if (name.equals(child.getUserObject())) {
					sections.expandPath(new TreePath(node.getPath()));
					sections.setSelectionPath(new TreePath(child.getPath()));
					return;
				}
			}
		}
	}

	public void addEditTemplatesPanel(BlocklyEditorType type) {
		addEditTemplatesPanel(type.registryName() + "_templates", "templates/" + type.extension(), type.extension());
	}

	public void addEditTemplatesPanel(String translationKey, String folder, String extension) {
		new EditTemplatesPanel(this, L10N.t("dialog.preferences.page_" + translationKey), folder, extension);
	}

	private void createPreferenceSection(String section) {
		String name = L10N.t("preferences.section." + section);
		String description = L10N.t("preferences.section." + section + ".description");
		addSection(name);

		JPanel sectionPanel = new JPanel(new GridBagLayout());
		sectionPanel.setOpaque(false);

		JComponent titlebar = L10N.label("dialog.preferences.description", name, description);
		titlebar.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

		JScrollPane scrollPane = new JScrollPane(PanelUtils.pullElementUp(sectionPanel));
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		preferences.add(PanelUtils.northAndCenterElement(titlebar, scrollPane, 0, 0), name);

		sectionPanels.put(section, sectionPanel);
	}

	private void savePreferences() {
		PreferencesManager.getPreferencesRegistry().forEach((_, preferences) -> preferences.forEach(entry -> {
			if (entries.containsKey(entry))
				entry.setValueFromComponent(entries.get(entry));
		}));
		PreferencesManager.savePreferences();
	}

	private JComponent generateEntryComponent(PreferencesEntry<?> entry, JPanel placeInside) {
		String name = L10N.t("preferences." + entry.getSectionKey() + "." + entry.getID());
		String description = L10N.t("preferences." + entry.getSectionKey() + "." + entry.getID() + ".description");
		if (description == null)
			description = "";

		JComponent label = L10N.label("dialog.preferences.entry_description", name, description);
		JComponent component = entry.getComponent(parent, _ -> markChanged());
		if (component != null) {
			JPanel wrap = new JPanel(new BorderLayout()) {
				@Override public Dimension getPreferredSize() {
					Dimension retval = super.getPreferredSize();
					retval.height = 32;
					if (!(component instanceof JCheckBox)) {
						retval.width = Math.max(retval.width, 128);
					}
					return retval;
				}
			};
			wrap.add("Center", component);
			placeInside.add(PanelUtils.westAndEastElement(label, PanelUtils.pullElementUp(wrap)), getConstraints());
		} else {
			placeInside.add(L10N.label("dialog.preferences.unknown_property_type", name), getConstraints());
		}
		return component;
	}

	private GridBagConstraints getConstraints() {
		GridBagConstraints cons = new GridBagConstraints();
		cons.anchor = GridBagConstraints.NORTH;
		cons.fill = GridBagConstraints.BOTH;
		cons.gridx = 0;
		cons.weightx = 1;
		cons.weighty = 1;
		cons.insets = new Insets(5, 10, 5, 10);
		return cons;
	}

	public void markChanged() {
		apply.setEnabled(true);
	}

}
