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

package net.mcreator.ui.dialogs.workspace;

import net.mcreator.element.ModElementType;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class GeneratorSelector {

	private static final String covpfx = "dialog.generator_selector.coverage.";

	public static GeneratorConfiguration getGeneratorSelector(Window parent, @Nullable GeneratorConfiguration current,
			@Nullable GeneratorFlavor currentFlavor) {
		JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

		JComboBox<CBoxEntry> generator = new JComboBox<>();

		mainPanel.add("North",
				PanelUtils.westAndCenterElement(L10N.label("dialog.generator_selector.current"), generator, 10, 10));

		CardLayout cardLayout = new CardLayout();
		JPanel statsPan = new JPanel(cardLayout);

		generator.setRenderer(new ConditionalComboBoxRenderer());

		for (GeneratorConfiguration generatorConfiguration : Generator.GENERATOR_CACHE.values()) {
			GeneratorStats stats = generatorConfiguration.getGeneratorStats();

			if (currentFlavor != null) {
				generator.addItem(new CBoxEntry(generatorConfiguration,
						currentFlavor.equals(generatorConfiguration.getGeneratorFlavor())));
			} else {
				generator.addItem(new CBoxEntry(generatorConfiguration));
			}

			JPanel genStats = new JPanel();
			genStats.setLayout(new BoxLayout(genStats, BoxLayout.PAGE_AXIS));

			genStats.add(PanelUtils.northAndCenterElement(
					L10N.label("dialog.generator_selector.generator_status", stats.getStatus().getName()),
					new JLabel()));

			genStats.add(new JEmptyBox(15, 15));

			JPanel baseCoverageInfo = new JPanel(new GridLayout(-1, 5, 7, 2));

			addStatusLabel(L10N.t(covpfx + "textures"), stats.getBaseCoverageInfo().get("textures"), baseCoverageInfo);
			addStatusLabel(L10N.t(covpfx + "sounds"), stats.getBaseCoverageInfo().get("sounds"), baseCoverageInfo);
			addStatusLabel(L10N.t(covpfx + "structures"), stats.getBaseCoverageInfo().get("structures"),
					baseCoverageInfo);
			addStatusLabel(L10N.t(covpfx + "translations"), stats.getBaseCoverageInfo().get("i18n"), baseCoverageInfo);

			if (generatorConfiguration.getGeneratorFlavor().getBaseLanguage() == GeneratorFlavor.BaseLanguage.JAVA)
				addStatusLabel(L10N.t(covpfx + "variables"), stats.getBaseCoverageInfo().get("variables"),
						baseCoverageInfo);

			if (generatorConfiguration.getGeneratorFlavor().getBaseLanguage() == GeneratorFlavor.BaseLanguage.JAVA)
				addStatusLabel(L10N.t(covpfx + "java_models"), stats.getBaseCoverageInfo().get("model_java"),
						baseCoverageInfo);

			addStatusLabel(L10N.t(covpfx + "json_models"), stats.getBaseCoverageInfo().get("model_json"),
					baseCoverageInfo);

			if (generatorConfiguration.getGeneratorFlavor() == GeneratorFlavor.FORGE)
				addStatusLabel(L10N.t(covpfx + "obj_models"), stats.getBaseCoverageInfo().get("model_obj"),
						baseCoverageInfo);

			genStats.add(PanelUtils
					.northAndCenterElement(L10N.label("dialog.generator_selector.features"), baseCoverageInfo, 10, 10));

			genStats.add(new JEmptyBox(20, 20));

			JPanel supportedModTypes = new JPanel(new GridLayout(-1, 5, 7, 2));
			for (Map.Entry<ModElementType, GeneratorStats.CoverageStatus> typeCoverageInfo : stats
					.getModElementTypeCoverageInfo().entrySet()) {
				addStatusLabel(typeCoverageInfo.getKey().getReadableName(), typeCoverageInfo.getValue(),
						supportedModTypes);
			}
			genStats.add(PanelUtils
					.northAndCenterElement(L10N.label("dialog.generator_selector.mod_element_types"), supportedModTypes,
							10, 10));

			genStats.add(new JEmptyBox(20, 20));

			JPanel supportedElements = new JPanel(new GridLayout(-1, 6, 7, 3));

			addStatsBar(L10N.t(covpfx + "achievements"), "achievements", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "biomes"), "biomes", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "blocksitems"), "blocksitems", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "tabs"), "tabs", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "damage_sources"), "damagesources", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "def_biome_features"), "defaultfeatures", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "enchantments"), "enchantments", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "enchantment_types"), "enchantmenttypes", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "entities"), "entities", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "fluids"), "fluids", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "game_modes"), "gamemodes", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "game_rules"), "gamerules", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "map_colors"), "mapcolors", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "materials"), "materials", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "particles"), "particles", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "path_node_types"), "pathnodetypes", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "potions"), "potions", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "sounds"), "sounds", supportedElements, stats);
			addStatsBar(L10N.t(covpfx + "step_sounds"), "stepsounds", supportedElements, stats);

			if (generatorConfiguration.getGeneratorFlavor() == GeneratorFlavor.FORGE)
				addStatsBar(L10N.t(covpfx + "biome_dictionary"), "biomedictionarytypes", supportedElements, stats);

			genStats.add(PanelUtils
					.northAndCenterElement(L10N.label("dialog.generator_selector.element_coverage"), supportedElements,
							10, 10));

			genStats.add(new JEmptyBox(20, 20));

			JPanel supportedProcedures = new JPanel(new GridLayout(-1, 4, 7, 3));
			addStatsBar(L10N.t(covpfx + "procedure_blocks"), "procedures", supportedProcedures, stats);
			addStatsBar(L10N.t(covpfx + "ai_tasks"), "aitasks", supportedProcedures, stats);
      addStatsBar(L10N.t(covpfx + "tooltips"), "tooltips", supportedProcedures, stats);
			addStatsBar(L10N.t(covpfx + "global_triggers"), "triggers", supportedProcedures, stats);
			addStatsBar(L10N.t(covpfx + "advancement_triggers"), "jsontriggers", supportedProcedures, stats);
			genStats.add(PanelUtils.northAndCenterElement(L10N.label("dialog.generator_selector.procedure_coverage"),
					supportedProcedures, 10, 10));

			JPanel genStatsW = new JPanel();
			genStatsW.setBorder(BorderFactory.createTitledBorder(L10N.t("dialog.generator_selector.generator_info")));
			genStatsW.add(PanelUtils.maxMargin(genStats, 5, true, true, true, false));
			statsPan.add(genStatsW, generatorConfiguration.getGeneratorName());
		}

		mainPanel.add("Center", statsPan);

		generator.addActionListener(new ActionListener() {
			CBoxEntry oldItem;

			@Override public void actionPerformed(ActionEvent e) {
				Object selectedItem = generator.getSelectedItem();
				if (selectedItem != null) {
					if (!((CBoxEntry) selectedItem).enabled) {
						generator.setSelectedItem(oldItem);
					} else {
						oldItem = (CBoxEntry) selectedItem;
					}
				}
				cardLayout.show(statsPan,
						((CBoxEntry) Objects.requireNonNull(generator.getSelectedItem())).generatorConfiguration
								.getGeneratorName());
			}
		});

		generator.setSelectedItem(new CBoxEntry(current));

		int resultval = JOptionPane.showConfirmDialog(parent, mainPanel, L10N.t("dialog.generator_selector.title"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (resultval == JOptionPane.OK_OPTION && generator.getSelectedItem() != null) {
			return ((CBoxEntry) generator.getSelectedItem()).generatorConfiguration;
		}
		return null;
	}

	private static void addStatusLabel(String readableName, GeneratorStats.CoverageStatus supported,
			JPanel baseCoverageInfo) {
		JLabel label = new JLabel(readableName);
		if (supported == GeneratorStats.CoverageStatus.FULL) {
			label.setIcon(UIRES.get("18px.ok"));
		} else if (supported == GeneratorStats.CoverageStatus.PARTIAL) {
			label.setIcon(UIRES.get("18px.warning"));
		}
		if (supported == GeneratorStats.CoverageStatus.NONE) {
			label.setIcon(UIRES.get("18px.remove"));
		}
		baseCoverageInfo.add(label);
	}

	private static void addStatsBar(String label, String registry, JPanel supportedElements, GeneratorStats stats) {
		JProgressBar bar = new JProgressBar();
		bar.setMaximum(100);
		bar.setPreferredSize(new Dimension(0, 0));

		if (stats.getCoverageInfo().get(registry) != null) {
			bar.setValue(stats.getCoverageInfo().get(registry).intValue());
			bar.setString(new DecimalFormat("#.##").format(stats.getCoverageInfo().get(registry)) + " %");
		}

		bar.setUI(new BasicProgressBarUI() {
			protected Color getSelectionBackground() {
				return (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR");
			}

			protected Color getSelectionForeground() {
				return (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT");
			}
		});

		bar.setStringPainted(true);

		ComponentUtils.deriveFont(bar, 10);

		if (bar.getValue() < 25)
			bar.setForeground(new Color(0xF98771));
		else if (bar.getValue() < 100)
			bar.setForeground(new Color(0xF5F984));
		else
			bar.setForeground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));

		supportedElements.add(new JLabel(label + ": "));
		supportedElements.add(bar);
	}

	static class ConditionalComboBoxRenderer implements ListCellRenderer<CBoxEntry> {

		private final BasicComboBoxRenderer renderer = new BasicComboBoxRenderer();

		@Override
		public Component getListCellRendererComponent(JList list, CBoxEntry value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel component = (JLabel) renderer
					.getListCellRendererComponent(list, value.generatorConfiguration, index, isSelected, cellHasFocus);
			if (!value.enabled) {
				component.setBackground(list.getBackground());
				component.setForeground(Color.gray.brighter());
				component.setText(L10N.t("dialog.generator_selector.different_type", component.getText(),
						value.generatorConfiguration.getGeneratorFlavor().name().toLowerCase(Locale.ENGLISH)));
			}
			return component;
		}
	}

	private static class CBoxEntry {
		GeneratorConfiguration generatorConfiguration;
		boolean enabled;

		CBoxEntry(GeneratorConfiguration generatorConfiguration) {
			this(generatorConfiguration, true);
		}

		CBoxEntry(GeneratorConfiguration generatorConfiguration, boolean enabled) {
			this.generatorConfiguration = generatorConfiguration;
			this.enabled = enabled;
		}

		@Override public boolean equals(Object o) {
			return o instanceof CBoxEntry && ((CBoxEntry) o).generatorConfiguration.equals(this.generatorConfiguration);
		}

		@Override public String toString() {
			return generatorConfiguration.toString();
		}
	}

}
