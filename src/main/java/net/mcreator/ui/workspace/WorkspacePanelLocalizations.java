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

package net.mcreator.ui.workspace;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.FileIO;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.util.image.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class WorkspacePanelLocalizations extends AbstractWorkspacePanel {

	private final JTabbedPane pane;
	private ArrayList<TableRowSorter<TableModel>> sorters = new ArrayList<>();

	private ChangeListener changeListener;

	private final JButton del;
	private final JButton exp;
	private final JButton imp;

	WorkspacePanelLocalizations(WorkspacePanel workspacePanel) {
		super(workspacePanel);

		pane = new JTabbedPane();
		pane.setOpaque(false);
		pane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
			@Override protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
			}
		});
		pane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		changeListener = e -> {
		};

		pane.addChangeListener(changeListener);

		add("Center", pane);

		TransparentToolBar bar = new TransparentToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		JButton add = L10N.button("workspace.localization.add_entry");
		add.setIcon(UIRES.get("16px.add.gif"));
		add.setContentAreaFilled(false);
		add.setOpaque(false);
		ComponentUtils.deriveFont(add, 12);
		add.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(add);

		del = L10N.button("workspace.localization.remove_selected");
		del.setIcon(UIRES.get("16px.delete.gif"));
		del.setOpaque(false);
		del.setContentAreaFilled(false);
		del.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(del);

		bar.addSeparator();

		exp = L10N.button("workspace.localization.export_to_csv");
		exp.setIcon(UIRES.get("16px.ext.gif"));
		exp.setOpaque(false);
		exp.setContentAreaFilled(false);
		exp.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(exp);

		imp = L10N.button("workspace.localization.import_csv");
		imp.setIcon(UIRES.get("16px.open.gif"));
		imp.setOpaque(false);
		imp.setContentAreaFilled(false);
		imp.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(imp);

		add("North", bar);

		add.addActionListener(e -> {
			String key = JOptionPane.showInputDialog(workspacePanel.getMCreator(),
					L10N.t("workspace.localization.key_name_message"), L10N.t("workspace.localization.key_name_title"),
					JOptionPane.QUESTION_MESSAGE);
			if (key != null && !key.equals("")) {
				workspacePanel.getMCreator().getWorkspace().setLocalization(key, "");
				reloadElements();
			}
		});
	}

	@Override public void reloadElements() {
		for (var al : del.getActionListeners())
			del.removeActionListener(al);

		for (var al : imp.getActionListeners())
			imp.removeActionListener(al);

		for (var al : exp.getActionListeners())
			exp.removeActionListener(al);

		pane.removeAll();
		sorters = new ArrayList<>();

		for (var entry : workspacePanel.getMCreator().getWorkspace().getLanguageMap().entrySet()) {
			ConcurrentHashMap<String, String> entries = entry.getValue();

			JTable elements = new JTable(new DefaultTableModel(
					new Object[] { L10N.t("workspace.localization.column_key"),
							"Localized text for " + entry.getKey() + (entry.getKey().equals("en_us") ?
									" - values in en_us might get overwritten!" :
									" - mappings can be edited here") }, 0));

			final Font textFont = new Font("Sans-Serif", Font.PLAIN, 13);
			elements.setFont(textFont);
			elements.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					var c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					c.setFont(textFont);
					return c;
				}
			});
			elements.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()) {
				final JComponent component = new JTextField();

				{
					component.setFont(textFont);
				}

				@Override
				public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
						int rowIndex, int vColIndex) {
					((JTextField) component).setText((String) value);
					return component;
				}

				@Override public Object getCellEditorValue() {
					return ((JTextField) component).getText();
				}
			});

			TableRowSorter<TableModel> sorter = new TableRowSorter<>(elements.getModel());
			elements.setRowSorter(sorter);
			sorters.add(sorter);

			elements.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			elements.setSelectionBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			elements.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			elements.setSelectionForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			elements.setBorder(BorderFactory.createEmptyBorder());
			elements.setGridColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			elements.setRowHeight(22);
			elements.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
			elements.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));

			JTableHeader header = elements.getTableHeader();
			header.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
			header.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));

			DefaultTableModel model = (DefaultTableModel) elements.getModel();
			for (Map.Entry<String, String> langs : entries.entrySet()) {
				model.addRow(new String[] { langs.getKey(), langs.getValue() });
			}

			// save values on table edit, do it in another thread
			// we add the listener after the values are inserted
			elements.getModel().addTableModelListener(e -> new Thread(() -> {
				if (e.getType() == TableModelEvent.UPDATE) {
					ConcurrentHashMap<String, String> keyValueMap = new ConcurrentHashMap<>();
					for (int i = 0; i < elements.getModel().getRowCount(); i++) {
						keyValueMap.put((String) elements.getModel().getValueAt(i, 0),
								(String) elements.getModel().getValueAt(i, 1));
					}
					workspacePanel.getMCreator().getWorkspace().updateLanguage(entry.getKey(), keyValueMap);
				}
			}).start());

			JScrollPane sp = new JScrollPane(elements);
			sp.setOpaque(false);
			sp.getViewport().setOpaque(false);
			sp.getVerticalScrollBar().setUnitIncrement(11);
			sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
					(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), sp.getVerticalScrollBar()));
			sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

			sp.setColumnHeaderView(null);

			final int id = pane.getTabCount();
			pane.addTab(entry.getKey(), null, sp);

			JPanel tab = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
			tab.setOpaque(false);
			JButton button = new JButton(UIRES.get("16px.delete.gif"));
			button.setContentAreaFilled(false);
			button.setBorder(BorderFactory.createEmptyBorder());
			button.setMargin(new Insets(0, 0, 0, 0));
			button.addActionListener(e -> {
				int n = JOptionPane.showConfirmDialog(workspacePanel.getMCreator(),
						L10N.t("workspace.localization.confirm_delete_map"), L10N.t("common.confirmation"),
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
				if (n == 0) {
					workspacePanel.getMCreator().getWorkspace().removeLocalizationLanguage(entry.getKey());
					reloadElements();
				}
			});
			button.setEnabled(!entry.getKey().equals("en_us"));

			String flagpath = "/flags/" + entry.getKey().split("_")[1].toUpperCase(Locale.ENGLISH) + ".png";
			JLabel label = new JLabel(" " + entry.getKey() + " ");
			ComponentUtils.deriveFont(label, 12);
			try {
				@SuppressWarnings("ConstantConditions") BufferedImage image = ImageIO.read(
						getClass().getResourceAsStream(flagpath));
				label.setIcon(new ImageIcon(ImageUtils.crop(image, new Rectangle(1, 2, 14, 11))));
			} catch (Exception ignored) { // flag not found, ignore
			}

			label.setBorder(BorderFactory.createEmptyBorder());
			tab.add(label);
			tab.add(button);
			pane.setTabComponentAt(id, tab);

			del.addActionListener(a -> deleteCurrentlySelected(elements, id));

			elements.addKeyListener(new KeyAdapter() {
				@Override public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_DELETE)
						deleteCurrentlySelected(elements, id);
				}
			});

			exp.addActionListener(e -> {
				if (pane.getSelectedIndex() != id)
					return;

				if (entry.getKey().equals("en_us")) {
					JOptionPane.showMessageDialog(workspacePanel.getMCreator(),
							L10N.t("workspace.localization.confirm_export"),
							L10N.t("workspace.localization.export_translation"), JOptionPane.WARNING_MESSAGE);
					return;
				}

				File expFile = FileDialogs.getSaveDialog(workspacePanel.getMCreator(), new String[] { ".csv" });
				if (expFile != null) {
					Map<String, String> en_us = workspacePanel.getMCreator().getWorkspace().getLanguageMap()
							.get("en_us");

					ByteArrayOutputStream csvResult = new ByteArrayOutputStream();
					Writer outputWriter = new OutputStreamWriter(csvResult);

					CsvWriter writer = new CsvWriter(outputWriter, new CsvWriterSettings());
					writer.writeHeaders("Translation key (DON'T EDIT)",
							"TRANSLATION IN " + entry.getKey() + " - EDIT THIS COLUMN",
							"English text (DON'T EDIT - reference only)");
					for (Map.Entry<String, String> langs : workspacePanel.getMCreator().getWorkspace().getLanguageMap()
							.get(entry.getKey()).entrySet())
						writer.writeRow(langs.getKey(), langs.getValue(), en_us.get(langs.getKey()));
					writer.close();

					FileIO.writeStringToFile("SEP=,\n" + csvResult.toString(StandardCharsets.UTF_8), expFile);
				}
			});

			imp.addActionListener(e -> {
				if (pane.getSelectedIndex() != id)
					return;

				if (entry.getKey().equals("en_us")) {
					JOptionPane.showMessageDialog(workspacePanel.getMCreator(),
							L10N.t("workspace.localization.warning_export"),
							L10N.t("workspace.localization.export_translation"), JOptionPane.WARNING_MESSAGE);
					return;
				}

				File impFile = FileDialogs.getOpenDialog(workspacePanel.getMCreator(), new String[] { ".csv" });
				if (impFile != null) {
					ConcurrentHashMap<String, String> en_us = workspacePanel.getMCreator().getWorkspace()
							.getLanguageMap().get("en_us");
					CsvParserSettings settings = new CsvParserSettings();
					settings.setDelimiterDetectionEnabled(true);
					CsvParser parser = new CsvParser(settings);
					List<String[]> rows = parser.parseAll(impFile, StandardCharsets.UTF_8);

					ConcurrentHashMap<String, String> keyValueMap = new ConcurrentHashMap<>();
					for (String[] row : rows) {
						if (row.length < 2)
							continue;

						String key = row[0];
						String value = row[1];

						if (en_us.containsKey(key) && value != null)
							keyValueMap.put(key, value);
					}

					workspacePanel.getMCreator().getWorkspace().updateLanguage(entry.getKey(), keyValueMap);

					SwingUtilities.invokeLater(this::reloadElements);
				}

			});
		}

		int lastid = pane.getTabCount();
		pane.addTab("", null, null);
		pane.setTabComponentAt(lastid, new JLabel(UIRES.get("16px.add.gif")));

		pane.removeChangeListener(changeListener);
		changeListener = e -> {
			if (pane.getSelectedIndex() == lastid) {
				pane.setSelectedIndex(0);
				newLocalizationDialog();
			}
		};
		pane.addChangeListener(changeListener);

		refilterElements();
	}

	private void deleteCurrentlySelected(JTable elements, int id) {
		if (elements.getSelectedRow() == -1 || pane.getSelectedIndex() != id)
			return;

		String key = (String) elements.getValueAt(elements.getSelectedRow(), 0);
		if (key != null) {
			int n = JOptionPane.showConfirmDialog(workspacePanel.getMCreator(),
					L10N.t("workspace.localization.confirm_delete_entry"), L10N.t("common.confirmation"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (n == 0) {
				Arrays.stream(elements.getSelectedRows()).mapToObj(el -> (String) elements.getValueAt(el, 0))
						.forEach(workspacePanel.getMCreator().getWorkspace()::removeLocalizationEntryByKey);
				reloadElements();
			}
		}
	}

	private void newLocalizationDialog() {
		Map<String, ConcurrentHashMap<String, String>> language_map = workspacePanel.getMCreator().getWorkspace()
				.getLanguageMap();

		Set<String> locales = new HashSet<>();

		Locale[] availableLocales = Locale.getAvailableLocales();
		List<String> countryCodes = Arrays.asList(Locale.getISOCountries());

		for (Locale locale : availableLocales) {
			if (countryCodes.contains(locale.getCountry())) {
				String key = locale.getLanguage().toLowerCase(Locale.ENGLISH) + "_" + locale.getCountry()
						.toLowerCase(Locale.ENGLISH);
				if (language_map.get(key) == null)
					locales.add(locale.getDisplayLanguage().trim() + ": " + key);
			}
		}

		ArrayList<String> sortedLocales = new ArrayList<>(locales);
		sortedLocales.sort(String::compareToIgnoreCase);

		String[] options = sortedLocales.toArray(new String[0]);
		String new_locale_id = (String) JOptionPane.showInputDialog(workspacePanel.getMCreator(),
				L10N.t("workspace.localization.language_choose"), L10N.t("workspace.localization.add_localization"),
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (new_locale_id != null) {
			String locale = new_locale_id.split(":")[1].trim();

			String based_from_id = (String) JOptionPane.showInputDialog(workspacePanel.getMCreator(),
					L10N.t("workspace.localization.language_copy"), L10N.t("workspace.localization.add_localization"),
					JOptionPane.QUESTION_MESSAGE, null, language_map.keySet().toArray(), "en_us");
			if (based_from_id != null) {
				ConcurrentHashMap<String, String> en_us = language_map.get(based_from_id);
				workspacePanel.getMCreator().getWorkspace().addLanguage(locale, en_us);
				reloadElements();
			}
		}
	}

	@Override public boolean isSupportedInWorkspace() {
		return workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("i18n")
				!= GeneratorStats.CoverageStatus.NONE;
	}

	@Override public void refilterElements() {
		for (TableRowSorter<TableModel> sorter : sorters)
			sorter.setRowFilter(RowFilter.regexFilter(workspacePanel.search.getText()));
	}

}
