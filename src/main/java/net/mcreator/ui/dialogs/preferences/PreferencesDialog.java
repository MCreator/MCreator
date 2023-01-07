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
import net.mcreator.preferences.Preferences;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.preferences.entries.PreferenceEntry;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.image.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PreferencesDialog extends MCreatorDialog {

	private static final Logger LOG = LogManager.getLogger("Preferences UI");

	DefaultListModel<String> model = new DefaultListModel<>();
	JPanel preferences = new JPanel();
	private ThemesPanel themes;

	private final Map<String, JPanel> sectionPanels = new HashMap<>();

	private final JList<String> sections = new JList<>(model);
	private final CardLayout preferencesLayout = new CardLayout();

	private final Map<PreferenceEntry<?>, JComponent> entries = new HashMap<>();

	private final JButton apply = L10N.button("action.common.apply");

	private final Window parent;

	public PreferencesDialog(Window parent, @Nullable String selectedTab) {
		super(parent);

		this.parent = parent;

		setModal(true);
		setTitle(L10N.t("dialog.preferences.title_mcreator"));

		sections.setBackground(getBackground());
		sections.setFixedCellHeight(26);
		sections.setBorder(new EmptyBorder(5, 10, 5, 0));

		preferences.setLayout(preferencesLayout);

		JSplitPane spne = new JSplitPane();
		spne.setRightComponent(preferences);
		spne.setLeftComponent(new JScrollPane(sections));
		spne.setContinuousLayout(true);
		spne.setUI(new BasicSplitPaneUI() {
			@Override public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					@Override public void setBorder(Border b) {
					}

					@Override public void paint(Graphics g) {
						g.setColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
						g.fillRect(0, 0, getSize().width, getSize().height);
						super.paint(g);
					}
				};
			}
		});
		spne.setContinuousLayout(true);
		spne.setDividerLocation(0.3);
		spne.setDividerSize(2);
		spne.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));
		add("Center", spne);

		sections.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		ComponentUtils.deriveFont(sections, 13);

		JButton ok = L10N.button("dialog.preferences.save");
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));

		JButton reset = L10N.button("dialog.preferences.restore_defaults");
		reset.addActionListener(actionEvent -> {
			int option = JOptionPane.showConfirmDialog(null, L10N.t("dialog.preferences.restore_defaults_confirmation"),
					L10N.t("dialog.preferences.restore_defaults"), JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null);
			if (option == JOptionPane.YES_OPTION) {
				PreferencesManager.reset();
				setVisible(false);
				new PreferencesDialog(parent, sections.getSelectedValue());
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

		sections.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sections.addListSelectionListener(e -> preferencesLayout.show(preferences, sections.getSelectedValue()));

		loadSections();

		if (selectedTab != null) {
			for (int i = 0; i < sections.getModel().getSize(); i++) {
				if (sections.getModel().getElementAt(i).equals(selectedTab))
					sections.setSelectedIndex(i);
			}
		}

		ok.addActionListener(event -> {
			savePreferences();
			setVisible(false);
		});

		apply.addActionListener(event -> {
			savePreferences();
			apply.setEnabled(false);
		});

		cancel.addActionListener(event -> setVisible(false));

		pack();
		setSize(Math.max(940, getBounds().width), 540);
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	private void loadSections() {
		// Add preference entries
		PreferencesManager.getPreferencesRegistry().forEach((identifier, preferences) -> preferences.stream()
				.filter(e -> !e.getSection().equals(Preferences.HIDDEN)).toList().forEach(entry -> {
					if (!sectionPanels.containsKey(entry.getSection()))
						createPreferenceSection(entry.getSection());
					entries.put(entry, generateEntryComponent(entry, sectionPanels.get(entry.getSection())));
				}));

		sections.setSelectedIndex(0);

		new PluginsPanel(this);

		themes = new ThemesPanel(this);

		addEditTemplatesPanel("ui_backgrounds", "backgrounds", "png");
		addEditTemplatesPanel("texture_templates", "templates/textures/texturemaker", "png");
		addEditTemplatesPanel("armor_templates", "templates/textures/armormaker", "png");

		BlocklyLoader.INSTANCE.getAllBlockLoaders().keySet().stream().filter(type -> type.extension() != null)
				.forEach(this::addEditTemplatesPanel);

		MCREvent.event(new PreferencesDialogEvent.SectionsLoaded(this));
	}

	public void addEditTemplatesPanel(BlocklyEditorType type) {
		addEditTemplatesPanel(type.registryName() + "_templates", "templates/" + type.extension(), type.extension());
	}

	public void addEditTemplatesPanel(String translationKey, String folder, String extension) {
		new EditTemplatesPanel(this, L10N.t("dialog.preferences.page_" + translationKey), folder, extension);
	}

	private void createPreferenceSection(String section) {
		if (section.equals(Preferences.HIDDEN))
			return;

		String name = L10N.t("preferences.section." + section);
		String description = L10N.t("preferences.section." + section + ".description");
		model.addElement(name);

		JPanel sectionPanel = new JPanel(new GridBagLayout());
		sectionPanel.setOpaque(false);

		JComponent titlebar = L10N.label("dialog.preferences.description", name, description);
		titlebar.setBorder(BorderFactory.createEmptyBorder(3, 10, 5, 10));

		JScrollPane scrollPane = new JScrollPane(PanelUtils.pullElementUp(sectionPanel));
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		preferences.add(PanelUtils.northAndCenterElement(titlebar, scrollPane), name);

		sectionPanels.put(section, sectionPanel);

	}

	private void savePreferences() {
		PreferencesManager.getPreferencesRegistry().forEach((identifier, preferences) -> {
			preferences.forEach(entry -> {
				JComponent component = entries.get(entry);
				if (component instanceof JSpinner spinner) {
					entry.setValue(spinner.getValue());
				} else if (component instanceof JCheckBox box) {
					entry.setValue(box.isSelected());
				} else if (component instanceof JComboBox<?> box) {
					entry.setValue(box.getSelectedItem());
				} else if (component instanceof JColor color) {
					entry.setValue(color.getColor());
				}
			});
		});
		PreferencesManager.PREFERENCES.uiTheme.setValue(themes.getSelectedTheme());
		PreferencesManager.savePreferences();
	}

	private JComponent generateEntryComponent(PreferenceEntry<?> entry, JPanel placeInside) {
		String name = L10N.t("preferences." + entry.getSection() + "." + entry.getID());
		String description = L10N.t("preferences." + entry.getSection() + "." + entry.getID() + ".description");
		if (description == null)
			description = "";

		JComponent label = L10N.label("dialog.preferences.entry_description", name, description);
		JComponent component = entry.getComponent(parent, e -> apply.setEnabled(true));
		if (component != null)
			placeInside.add(PanelUtils.westAndEastElement(label, component), getConstraints());
		else
			placeInside.add(L10N.label("dialog.preferences.unknown_property_type", name), getConstraints());
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

	public static class LocaleListRenderer extends JLabel implements ListCellRenderer<Locale> {

		private int uiTextsPercent = 0;
		private int helpTipsPercent = 0;

		@Override
		public Component getListCellRendererComponent(JList<? extends Locale> list, Locale value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setOpaque(isSelected);
			setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
			setForeground(Color.white);
			setBorder(new EmptyBorder(0, 1, 0, 0));

			ComponentUtils.deriveFont(this, 12);
			setText(" " + value.getDisplayName(Locale.ROOT));

			uiTextsPercent = L10N.getUITextsLocaleSupport(value);
			helpTipsPercent = L10N.getHelpTipsSupport(value);

			try {
				String flagpath = "/flags/" + value.toString().split("_")[1].toUpperCase(Locale.ENGLISH) + ".png";
				@SuppressWarnings("ConstantConditions") BufferedImage image = ImageIO.read(
						getClass().getResourceAsStream(flagpath));
				setIcon(new ImageIcon(ImageUtils.crop(image, new Rectangle(1, 2, 14, 11))));
			} catch (Exception ignored) { // flag not found, ignore
			}

			return this;
		}

		@Override public Dimension getPreferredSize() {
			return new Dimension(super.getPreferredSize().width, super.getPreferredSize().height + 15);
		}

		@Override protected void paintComponent(Graphics gx) {
			Graphics2D g = (Graphics2D) gx;

			g.translate(0, -5);
			super.paintComponent(g);
			g.translate(0, 5);

			g.setColor(Color.lightGray);
			g.fillRect(0, getHeight() - 11, getWidth(), 11);

			g.setColor(Color.getHSBColor((float) (1 / 3d - ((100 - uiTextsPercent) / 3d / 100d)), 0.65f, 0.9f));
			g.fillRect(0, getHeight() - 11, (int) ((getWidth() / 2 - 2) * (uiTextsPercent / 100d)), 11);

			g.setColor(Color.getHSBColor((float) (1 / 3d - ((100 - helpTipsPercent) / 3d / 100d)), 0.65f, 0.9f));
			g.fillRect(getWidth() / 2 + 2, getHeight() - 11, (int) ((getWidth() / 2 - 2) * (helpTipsPercent / 100d)),
					11);

			g.setFont(getFont().deriveFont(9f));
			g.setColor(Color.darkGray);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g.drawString("Texts: " + uiTextsPercent + "%", 2, getHeight() - 2);
			g.drawString("Tips: " + helpTipsPercent + "%", getWidth() / 2 + 2 + 2, getHeight() - 2);
		}

	}

}
