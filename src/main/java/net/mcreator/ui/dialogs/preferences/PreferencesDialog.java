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

import net.mcreator.preferences.PreferencesData;
import net.mcreator.preferences.PreferencesEntry;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.preferences.PreferencesSection;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.image.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;

public class PreferencesDialog extends MCreatorDialog {

	private static final Logger LOG = LogManager.getLogger("Preferences UI");

	DefaultListModel<String> model = new DefaultListModel<>();
	JPanel preferences = new JPanel();
	private final ThemesPanel themes; // We create the variable here, so we can access it when we store tha preferences

	private final JList<String> sections = new JList<>(model);
	private final CardLayout preferencesLayout = new CardLayout();

	private final Map<PreferencesUnit, JComponent> entries = new HashMap<>();

	private final JButton apply = L10N.button("action.common.apply");

	private final Window parent;

	public PreferencesDialog(Window parent, String selectedTab) {
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
		JButton cancel = L10N.button(UIManager.getString("OptionPane.cancelButtonText"));

		JButton reset = L10N.button("dialog.preferences.restore_defaults");
		reset.addActionListener(actionEvent -> {
			int option = JOptionPane.showConfirmDialog(null, L10N.t("dialog.preferences.restore_defaults_confirmation"),
					L10N.t("dialog.preferences.restore_defaults"), JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null);
			if (option == JOptionPane.YES_OPTION) {
				PreferencesManager.PREFERENCES = new PreferencesData();
				PreferencesManager.storePreferences(PreferencesManager.PREFERENCES);
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
		themes = new ThemesPanel(this);

		if (selectedTab != null) {
			for (int i = 0; i < sections.getModel().getSize(); i++) {
				if (sections.getModel().getElementAt(i).equals(selectedTab))
					sections.setSelectedIndex(i);
			}
		}

		ok.addActionListener(event -> {
			storePreferences();
			setVisible(false);
		});

		apply.addActionListener(event -> {
			storePreferences();
			apply.setEnabled(false);
		});

		cancel.addActionListener(event -> setVisible(false));

		pack();
		setSize(Math.max(940, getBounds().width), 540);
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	private void loadSections() {
		Field[] fields = PreferencesData.class.getFields();
		for (Field field : fields) {
			PreferencesSection section = field.getAnnotation(PreferencesSection.class);
			if (section != null) {
				createPreferencesPanel(field);
			}
		}
		sections.setSelectedIndex(0);

		new EditTemplatesPanel(this, L10N.t("dialog.preferences.page_ui_backgrounds"), "backgrounds", "png");
		new EditTemplatesPanel(this, L10N.t("dialog.preferences.page_procedure_templates"), "templates/ptpl", "ptpl");
		new EditTemplatesPanel(this, L10N.t("dialog.preferences.page_ai_builder_templates"), "templates/aitpl",
				"aitpl");
		new EditTemplatesPanel(this, L10N.t("dialog.preferences.page_texture_templates"),
				"templates/textures/texturemaker", "png");
		new EditTemplatesPanel(this, L10N.t("dialog.preferences.page_armor_templates"), "templates/textures/armormaker",
				"png");
		new PluginsPanel(this);

	}

	private void createPreferencesPanel(Field sectionField) {
		String sectionid = sectionField.getName();

		String name = L10N.t("preferences.section." + sectionid);
		String description = L10N.t("preferences.section." + sectionid + ".description");

		model.addElement(name);

		JPanel sectionPanel = new JPanel(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		cons.anchor = GridBagConstraints.NORTH;
		cons.fill = GridBagConstraints.BOTH;
		cons.gridx = 0;
		cons.weightx = 1;
		cons.weighty = 1;
		cons.insets = new Insets(5, 10, 15, 10);
		sectionPanel.setOpaque(false);
		sectionPanel.add(L10N.label("dialog.preferences.description", name, description), cons);
		cons.insets = new Insets(5, 10, 5, 10);

		Field[] fields = sectionField.getType().getFields();
		for (Field field : fields) {
			PreferencesEntry entry = field.getAnnotation(PreferencesEntry.class);
			try {
				Object sectionInstance = sectionField.get(PreferencesManager.PREFERENCES); // load actual data
				Object value = field.get(sectionInstance);
				JComponent component = generateEntryComponent(field, sectionid, entry, value, sectionPanel, cons);
				entries.put(new PreferencesUnit(sectionField, field), component);
			} catch (IllegalAccessException e) {
				LOG.info("Reflection error: " + e.getMessage());
			}
		}
		preferences.add(new JScrollPane(PanelUtils.pullElementUp(sectionPanel)), name);
	}

	private void storePreferences() {
		PreferencesData data = PreferencesManager.PREFERENCES;
		for (Map.Entry<PreferencesUnit, JComponent> entry : entries.entrySet()) {
			PreferencesUnit unit = entry.getKey();
			try {
				Object sectionInstance = unit.section.get(data); // load actual data
				Object value = getValueFromComponent(unit.entry.getType(), entry.getValue());
				if (value != null)
					unit.entry.set(sectionInstance, value);
			} catch (IllegalAccessException e) {
				LOG.info("Reflection error: " + e.getMessage());
			}
		}
		data.hidden.uiTheme = themes.getThemes();
		PreferencesManager.storePreferences(data);
	}

	private JComponent generateEntryComponent(Field actualField, String sectionid, PreferencesEntry entry, Object value,
			JPanel placeInside, GridBagConstraints cons) {
		String fieldName = actualField.getName();
		String name = L10N.t("preferences." + sectionid + "." + fieldName);
		String description = L10N.t("preferences." + sectionid + "." + fieldName + ".description");

		if (description == null)
			description = "";

		JComponent label = L10N.label("dialog.preferences.entry_description", name, description);

		if (actualField.getType().equals(int.class) || actualField.getType().equals(Integer.class)) {
			int max = (int) entry.max();
			if (entry.meta().equals("max:maxram")) {
				max = ((int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean())
						.getTotalPhysicalMemorySize() / 1048576)) - 1024;
			}
			value = Math.max(entry.min(), Math.min(max, (Integer) value));
			SpinnerNumberModel model = new SpinnerNumberModel((int) Math.round((double) value), (int) entry.min(), max,
					1);
			JSpinner spinner = new JSpinner(model);
			spinner.addChangeListener(e -> apply.setEnabled(true));
			placeInside.add(PanelUtils.westAndEastElement(label, spinner), cons);
			return spinner;
		} else if (actualField.getType().equals(boolean.class) || actualField.getType().equals(Boolean.class)) {
			JCheckBox box = new JCheckBox();
			box.setSelected((boolean) value);
			box.addActionListener(e -> apply.setEnabled(true));
			placeInside.add(PanelUtils.westAndEastElement(label, box), cons);
			return box;
		} else if (actualField.getType().equals(String.class)) {
			JComboBox<String> box = new JComboBox<>(entry.arrayData());
			if (entry.visualWidth() != -1)
				box.setPreferredSize(new Dimension(entry.visualWidth(), 0));
			box.setEditable(entry.arrayDataEditable());
			box.setSelectedItem(value);
			box.addActionListener(e -> apply.setEnabled(true));
			placeInside.add(PanelUtils.westAndEastElement(label, box), cons);
			return box;
		} else if (actualField.getType().equals(Color.class)) {
			JColor box = new JColor(parent);
			if (entry.visualWidth() != -1)
				box.setPreferredSize(new Dimension(entry.visualWidth(), 0));
			box.setColor((Color) value);
			box.setColorSelectedListener(e -> apply.setEnabled(true));
			placeInside.add(PanelUtils.westAndEastElement(label, box), cons);
			return box;
		} else if (actualField.getType().equals(Locale.class)) {
			List<Locale> locales = new ArrayList<>(L10N.getSupportedLocales());
			locales.sort((a, b) -> {
				int sa = L10N.getLocaleSupport(a);
				int sb = L10N.getLocaleSupport(b);
				if (sa == sb)
					return a.getDisplayName().compareTo(b.getDisplayName());

				return sb - sa;
			});
			JComboBox<Locale> box = new JComboBox<>(locales.toArray(new Locale[0]));
			box.setRenderer(new LocaleListRenderer());
			box.setSelectedItem(value);
			box.addActionListener(e -> apply.setEnabled(true));
			placeInside.add(PanelUtils.westAndEastElement(label, box), cons);
			return box;
		}

		placeInside.add(L10N.label("dialog.preferences.unknown_property_type", name), cons);
		return null;
	}

	private Object getValueFromComponent(Class<?> type, JComponent value) {
		if (type.equals(int.class) || type.equals(Integer.class)) {
			return ((JSpinner) value).getValue();
		} else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return ((JCheckBox) value).isSelected();
		} else if (type.equals(String.class)) {
			return ((JComboBox<?>) value).getSelectedItem();
		} else if (type.equals(Color.class)) {
			return ((JColor) value).getColor();
		} else if (type.equals(Locale.class)) {
			return ((JComboBox<?>) value).getSelectedItem();
		}
		return null;
	}

	public void markChanged() {
		apply.setEnabled(true);
	}

	private static class PreferencesUnit {

		Field entry;
		Field section;

		PreferencesUnit(Field section, Field entry) {
			this.section = section;
			this.entry = entry;
		}
	}

	private static class LocaleListRenderer extends JLabel implements ListCellRenderer<Locale> {

		private int percent = 0;

		@Override
		public Component getListCellRendererComponent(JList<? extends Locale> list, Locale value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setOpaque(isSelected);
			setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
			setForeground(Color.white);
			setBorder(new EmptyBorder(0, 1, 0, 0));

			ComponentUtils.deriveFont(this, 12);
			setText(" " + value.getDisplayName(Locale.ROOT));

			percent = L10N.getLocaleSupport(value);

			try {
				String flagpath = "/flags/" + value.toString().split("_")[1].toUpperCase(Locale.ENGLISH) + ".png";
				BufferedImage image = ImageIO.read(getClass().getResourceAsStream(flagpath));
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

			g.setColor(Color.getHSBColor((float) (1 / 3d - ((100 - percent) / 3d / 100d)), 0.65f, 0.9f));
			g.fillRect(0, getHeight() - 11, (int) (getWidth() * (percent / 100d)), 11);

			g.setFont(getFont().deriveFont(9f));
			g.setColor(Color.darkGray);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawString("Coverage: " + percent + "%", 2, getHeight() - 2);
		}

	}

}
