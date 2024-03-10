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

import net.mcreator.Launcher;
import net.mcreator.element.ModElementType;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class GeneratorSelector {

	public static final String covpfx = "dialog.generator_selector.coverage.";

	private static final List<GeneratorFlavor> compatible1 = List.of(GeneratorFlavor.FORGE, GeneratorFlavor.FABRIC,
			GeneratorFlavor.NEOFORGE, GeneratorFlavor.QUILT);

	/**
	 * <p>Open a dialog window to select a {@link Generator} from the loaded generators. </p>
	 *
	 * @param parent        <p>The  window to attach the dialog</p>
	 * @param current       <p>The current generator settings used</p>
	 * @param currentFlavor <p>This is the current type of generator to use for the generator list.</p>
	 * @return <p>The {@link GeneratorConfiguration} to use</p>
	 */
	public static GeneratorConfiguration getGeneratorSelector(Window parent, @Nullable GeneratorConfiguration current,
			@Nullable GeneratorFlavor currentFlavor, boolean newWorkspace) {
		JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

		JComboBox<GeneratorConfiguration> generator = new JComboBox<>();

		mainPanel.add("North",
				PanelUtils.westAndCenterElement(L10N.label("dialog.generator_selector.current"), generator, 10, 10));

		CardLayout cardLayout = new CardLayout();
		JPanel statsPan = new JPanel(cardLayout);

		for (GeneratorConfiguration generatorConfiguration : Generator.GENERATOR_CACHE.values()) {
			GeneratorStats stats = generatorConfiguration.getGeneratorStats();

			if (currentFlavor == null || currentFlavor.equals(generatorConfiguration.getGeneratorFlavor())) {
				generator.addItem(generatorConfiguration);
			} else if (compatible1.contains(currentFlavor) && compatible1.contains(
					generatorConfiguration.getGeneratorFlavor()) && !newWorkspace) {
				generator.addItem(generatorConfiguration);
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
			addStatusLabel(L10N.t(covpfx + "tags"), stats.getBaseCoverageInfo().get("tags"), baseCoverageInfo);

			if (generatorConfiguration.getGeneratorFlavor().getBaseLanguage() == GeneratorFlavor.BaseLanguage.JAVA)
				addStatusLabel(L10N.t(covpfx + "variables"), stats.getBaseCoverageInfo().get("variables"),
						baseCoverageInfo);

			if (generatorConfiguration.getGeneratorFlavor().getBaseLanguage() == GeneratorFlavor.BaseLanguage.JAVA)
				addStatusLabel(L10N.t(covpfx + "java_models"), stats.getBaseCoverageInfo().get("model_java"),
						baseCoverageInfo);

			addStatusLabel(L10N.t(covpfx + "json_models"), stats.getBaseCoverageInfo().get("model_json"),
					baseCoverageInfo);

			if (generatorConfiguration.getGeneratorFlavor() == GeneratorFlavor.FORGE
					|| generatorConfiguration.getGeneratorFlavor() == GeneratorFlavor.NEOFORGE)
				addStatusLabel(L10N.t(covpfx + "obj_models"), stats.getBaseCoverageInfo().get("model_obj"),
						baseCoverageInfo);

			genStats.add(
					PanelUtils.northAndCenterElement(L10N.label("dialog.generator_selector.features"), baseCoverageInfo,
							10, 10));

			genStats.add(new JEmptyBox(20, 20));

			JPanel supportedModTypes = new JPanel(new GridLayout(-1, 5, 7, 2));
			for (Map.Entry<ModElementType<?>, GeneratorStats.CoverageStatus> typeCoverageInfo : stats.getModElementTypeCoverageInfo()
					.entrySet()) {
				addStatusLabel(typeCoverageInfo.getKey().getReadableName(), typeCoverageInfo.getValue(),
						supportedModTypes);
			}
			genStats.add(PanelUtils.northAndCenterElement(L10N.label("dialog.generator_selector.mod_element_types"),
					supportedModTypes, 10, 10));

			genStats.add(new JEmptyBox(20, 20));

			JPanel supportedElements = new JPanel(new GridLayout(-1, 6, 7, 3));
			DataListLoader.getCache().entrySet().stream().filter(e -> !e.getValue().isEmpty()).map(Map.Entry::getKey)
					.sorted().forEach(e -> {
						String name = L10N.t(covpfx + e);
						if (name != null)
							addStatsBar(name, e, supportedElements, stats);
					});

			genStats.add(PanelUtils.northAndCenterElement(L10N.label("dialog.generator_selector.element_coverage"),
					supportedElements, 10, 10));

			genStats.add(new JEmptyBox(20, 20));

			JPanel supportedProcedures = new JPanel(new GridLayout(-1, 4, 7, 3));

			stats.getGeneratorBlocklyBlocks().forEach((key, value) -> {
				String name = L10N.t(covpfx + key);
				if (name != null)
					addStatsBar(name, key, supportedProcedures, stats);
			});

			addStatsBar(L10N.t(covpfx + "triggers"), "triggers", supportedProcedures, stats);

			genStats.add(PanelUtils.northAndCenterElement(L10N.label("dialog.generator_selector.procedure_coverage"),
					supportedProcedures, 10, 10));

			JPanel genStatsW = new JPanel();
			genStatsW.setBorder(BorderFactory.createTitledBorder(L10N.t("dialog.generator_selector.generator_info")));
			genStatsW.add(PanelUtils.maxMargin(genStats, 5, true, true, true, false));
			statsPan.add(genStatsW, generatorConfiguration.getGeneratorName());
		}

		JScrollPane pane = new JScrollPane(statsPan);
		pane.getVerticalScrollBar().setUnitIncrement(10);

		mainPanel.add("Center", pane);

		generator.addActionListener(e -> {
			if (generator.getSelectedItem() instanceof GeneratorConfiguration generatorConfiguration)
				cardLayout.show(statsPan, generatorConfiguration.getGeneratorName());
		});

		generator.setSelectedItem(current);

		generator.addActionListener(new ActionListener() {
			GeneratorConfiguration oldItem = current;

			@Override public void actionPerformed(ActionEvent e) {
				if (generator.getSelectedItem() instanceof GeneratorConfiguration generatorConfiguration) {
					if (!Launcher.version.isDevelopment() && !newWorkspace && generatorConfiguration != current
							&& generatorConfiguration.getGeneratorStats().getStatus() == GeneratorStats.Status.DEV) {
						generator.setSelectedItem(oldItem);
						JOptionPane.showMessageDialog(parent, L10N.t("dialog.generator_selector.dev_gen_message"),
								L10N.t("dialog.generator_selector.dev_gen_title"), JOptionPane.WARNING_MESSAGE);
					} else {
						oldItem = generatorConfiguration;
					}
				}
			}
		});

		mainPanel.addHierarchyListener(e -> {
			if (SwingUtilities.getWindowAncestor(mainPanel) instanceof Dialog dialog) {
				if (dialog.getHeight() > dialog.getGraphicsConfiguration().getBounds().height - 32) {
					dialog.setSize(dialog.getWidth(), dialog.getGraphicsConfiguration().getBounds().height - 32);
					dialog.setLocationRelativeTo(parent);
				}
			}
		});

		int resultval = JOptionPane.showConfirmDialog(parent, mainPanel, L10N.t("dialog.generator_selector.title"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (resultval == JOptionPane.OK_OPTION && generator.getSelectedItem() != null) {
			return (GeneratorConfiguration) generator.getSelectedItem();
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

		bar.setStringPainted(true);

		ComponentUtils.deriveFont(bar, 10);

		if (bar.getValue() < 25)
			bar.setForeground(new Color(0xF98771));
		else if (bar.getValue() < 100)
			bar.setForeground(new Color(0xF5F984));
		else
			bar.setForeground(Theme.current().getInterfaceAccentColor());

		supportedElements.add(new JLabel(label + ": "));
		supportedElements.add(bar);
	}

}
