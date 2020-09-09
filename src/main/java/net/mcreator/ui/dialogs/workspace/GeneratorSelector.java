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

	public static GeneratorConfiguration getGeneratorSelector(Window parent, @Nullable GeneratorConfiguration current,
			@Nullable GeneratorFlavor currentFlavor) {
		JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

		JComboBox<CBoxEntry> generator = new JComboBox<>();

		mainPanel.add("North",
				PanelUtils.westAndCenterElement(new JLabel("<html><b>&nbsp;Selected generator:"), generator, 10, 10));

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

			genStats.add(PanelUtils.northAndCenterElement(new JLabel("<html>Status: <b>" + stats.getStatus().getName()),
					new JLabel()));

			genStats.add(new JEmptyBox(15, 15));

			JPanel baseCoverageInfo = new JPanel(new GridLayout(-1, 5, 7, 2));

			addStatusLabel("Textures", stats.getBaseCoverageInfo().get("textures"), baseCoverageInfo);
			addStatusLabel("Sounds", stats.getBaseCoverageInfo().get("sounds"), baseCoverageInfo);
			addStatusLabel("Structures", stats.getBaseCoverageInfo().get("structures"), baseCoverageInfo);
			addStatusLabel("Translations", stats.getBaseCoverageInfo().get("i18n"), baseCoverageInfo);

			if (generatorConfiguration.getGeneratorFlavor().getBaseLanguage() == GeneratorFlavor.BaseLanguage.JAVA)
				addStatusLabel("Variables", stats.getBaseCoverageInfo().get("variables"), baseCoverageInfo);

			if (generatorConfiguration.getGeneratorFlavor().getBaseLanguage() == GeneratorFlavor.BaseLanguage.JAVA)
				addStatusLabel("Java 3D models", stats.getBaseCoverageInfo().get("model_java"), baseCoverageInfo);

			addStatusLabel("JSON 3D models", stats.getBaseCoverageInfo().get("model_json"), baseCoverageInfo);

			if (generatorConfiguration.getGeneratorFlavor() == GeneratorFlavor.FORGE)
				addStatusLabel("OBJ 3D models", stats.getBaseCoverageInfo().get("model_obj"), baseCoverageInfo);

			genStats.add(PanelUtils.northAndCenterElement(new JLabel("Feature overview:"), baseCoverageInfo, 10, 10));

			genStats.add(new JEmptyBox(20, 20));

			JPanel supportedModTypes = new JPanel(new GridLayout(-1, 5, 7, 2));
			for (Map.Entry<ModElementType, GeneratorStats.CoverageStatus> typeCoverageInfo : stats
					.getModElementTypeCoverageInfo().entrySet()) {
				addStatusLabel(typeCoverageInfo.getKey().getReadableName(), typeCoverageInfo.getValue(),
						supportedModTypes);
			}
			genStats.add(PanelUtils
					.northAndCenterElement(new JLabel("Supported mod element types:"), supportedModTypes, 10, 10));

			genStats.add(new JEmptyBox(20, 20));

			JPanel supportedElements = new JPanel(new GridLayout(-1, 6, 7, 3));

			addStatsBar("Achievements", "achievements", supportedElements, stats);
			addStatsBar("Action Animations", "actionanimations", supportedElements, stats);
			addStatsBar("Biomes", "biomes", supportedElements, stats);
			addStatsBar("Blocks and items", "blocksitems", supportedElements, stats);
			addStatsBar("Creative tabs", "tabs", supportedElements, stats);
			addStatsBar("Damage sources", "damagesources", supportedElements, stats);
			addStatsBar("Def. biome features", "defaultfeatures", supportedElements, stats);
			addStatsBar("Enchantment", "enchantments", supportedElements, stats);
			addStatsBar("Enchantment types", "enchantmenttypes", supportedElements, stats);
			addStatsBar("Entities", "entities", supportedElements, stats);
			addStatsBar("Fluids", "fluids", supportedElements, stats);
			addStatsBar("Game modes", "gamemodes", supportedElements, stats);
			addStatsBar("Map colors", "mapcolors", supportedElements, stats);
			addStatsBar("Materials", "materials", supportedElements, stats);
			addStatsBar("Particles", "particles", supportedElements, stats);
			addStatsBar("Path node types", "pathnodetypes", supportedElements, stats);
			addStatsBar("Potions", "potions", supportedElements, stats);
			addStatsBar("Sounds", "sounds", supportedElements, stats);
			addStatsBar("Step sounds", "stepsounds", supportedElements, stats);


			if (generatorConfiguration.getGeneratorFlavor() == GeneratorFlavor.FORGE)
				addStatsBar("Biome dictionary", "biomedictionarytypes", supportedElements, stats);

			genStats.add(PanelUtils.northAndCenterElement(
					new JLabel("Vanilla/Forge elements coverage (compared to the latest supported Minecraft version):"),
					supportedElements, 10, 10));

			genStats.add(new JEmptyBox(20, 20));

			JPanel supportedProcedures = new JPanel(new GridLayout(-1, 4, 7, 3));
			addStatsBar("Procedure blocks", "procedures", supportedProcedures, stats);
			addStatsBar("AI tasks / goals", "aitasks", supportedProcedures, stats);
			addStatsBar("Global triggers", "triggers", supportedProcedures, stats);
			addStatsBar("Advancement triggers", "jsontriggers", supportedProcedures, stats);
			genStats.add(PanelUtils
					.northAndCenterElement(new JLabel("Procedure system coverage:"), supportedProcedures, 10, 10));

			JPanel genStatsW = new JPanel();
			genStatsW.setBorder(BorderFactory.createTitledBorder("Generator info"));
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

		int resultval = JOptionPane
				.showConfirmDialog(parent, mainPanel, "Generator selector", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);

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
				component.setText(
						"<html>" + component.getText() + "<small> - different type: " + value.generatorConfiguration
								.getGeneratorFlavor().name().toLowerCase(Locale.ENGLISH));
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
