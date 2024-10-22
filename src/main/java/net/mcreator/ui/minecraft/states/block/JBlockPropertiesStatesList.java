/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.states.block;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.TechnicalButton;
import net.mcreator.ui.component.entries.JEntriesList;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.AddBlockPropertyDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.Validator;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.mcreator.ui.minecraft.states.block.BlockStatePropertyUtils.MAX_PROPERTY_COMBINATIONS;

public class JBlockPropertiesStatesList extends JEntriesList {

	private final Supplier<Collection<String>> nonUserProvidedProperties;

	private final List<JBlockPropertiesListEntry> propertiesList = new ArrayList<>();

	private final JPanel propertyEntries = new JPanel();

	private final TechnicalButton addProperty = new TechnicalButton(UIRES.get("16px.add"));
	private final TechnicalButton addExisting = new TechnicalButton(UIRES.get("16px.add"));

	private int propertyCombinations = 1;
	private final JProgressBar propertiesCap = new JProgressBar();
	private final JLabel propertiesCapLabel = new JLabel();

	public JBlockPropertiesStatesList(MCreator mcreator, IHelpContext gui,
			Supplier<Collection<String>> nonUserProvidedProperties) {
		super(mcreator, new BorderLayout(0, 10), gui);
		this.nonUserProvidedProperties = nonUserProvidedProperties;

		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

		propertyEntries.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		propertyEntries.setLayout(new GridLayout(0, 1, 5, 5));
		propertyEntries.setOpaque(false);

		addProperty.setText(L10N.t("elementgui.block.custom_properties.add"));
		addProperty.addActionListener(e -> createPropertiesEntry());
		addExisting.setText(L10N.t("elementgui.block.custom_properties.add_existing"));
		addExisting.addActionListener(e -> addPropertyFromDataList());

		JScrollPane scrollProperties = new JScrollPane(PanelUtils.pullElementUp(propertyEntries)) {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(Theme.current().getAltBackgroundColor());
				g2d.setComposite(AlphaComposite.SrcOver.derive(0.45f));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		scrollProperties.getVerticalScrollBar().setUnitIncrement(15);
		scrollProperties.setOpaque(false);
		scrollProperties.getViewport().setOpaque(false);

		propertiesCap.setMaximum(MAX_PROPERTY_COMBINATIONS);
		propertiesCap.setPreferredSize(new Dimension(220, 6));

		JPanel mainContent = new JPanel(new BorderLayout());
		mainContent.setOpaque(false);
		mainContent.add("North", PanelUtils.join(FlowLayout.LEFT, 0, 5, addProperty, new JEmptyBox(5, 5), addExisting,
				new JEmptyBox(5, 5), HelpUtils.helpButton(gui.withEntry("block/block_states")), new JEmptyBox(15, 5),
				L10N.label("elementgui.block.custom_properties.properties_cap"), new JEmptyBox(10, 5), propertiesCap,
				new JEmptyBox(10, 5), propertiesCapLabel));
		mainContent.add("Center", scrollProperties);

		JPanel basePane = new JPanel(new GridLayout());
		basePane.setOpaque(false);
		basePane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.custom_properties_states"), 0, 0, basePane.getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));
		basePane.add(mainContent);
		add("Center", basePane);

		recalculatePropertiesCap();
	}

	private void recalculatePropertiesCap() {
		propertyCombinations = BlockStatePropertyUtils.getNumberOfPropertyCombinations(
				propertiesList.stream().map(JBlockPropertiesListEntry::getEntry).map(PropertyDataWithValue::property)
						.collect(Collectors.toList()));

		int cappedPropertyCombinations = Math.min(propertyCombinations, MAX_PROPERTY_COMBINATIONS);
		propertiesCap.setValue(cappedPropertyCombinations);

		if (cappedPropertyCombinations < MAX_PROPERTY_COMBINATIONS * 0.5) {
			propertiesCap.setForeground(Validator.ValidationResultType.PASSED.getColor());
		} else { // blend color between warn and error
			Color warn = Validator.ValidationResultType.WARNING.getColor();
			Color error = Validator.ValidationResultType.ERROR.getColor();
			float blend = (cappedPropertyCombinations - MAX_PROPERTY_COMBINATIONS * 0.5f) / (MAX_PROPERTY_COMBINATIONS
					* 0.5f);
			propertiesCap.setForeground(new Color((int) (warn.getRed() + blend * (error.getRed() - warn.getRed())),
					(int) (warn.getGreen() + blend * (error.getGreen() - warn.getGreen())),
					(int) (warn.getBlue() + blend * (error.getBlue() - warn.getBlue()))));
		}

		propertiesCapLabel.setText(String.valueOf(propertyCombinations));
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		addProperty.setEnabled(enabled);
		addExisting.setEnabled(enabled);

		propertiesList.forEach(e -> e.setEnabled(enabled));

		propertyEntries.setEnabled(enabled);
	}

	private void createPropertiesEntry() {
		PropertyDataWithValue<?> newEntry = AddBlockPropertyDialog.showCreateDialog(mcreator,
				propertiesList.stream().map(JBlockPropertiesListEntry::getPropertyData).collect(Collectors.toList()),
				nonUserProvidedProperties);
		if (newEntry != null) {
			addPropertiesEntry(newEntry);
		}
	}

	private void addPropertyFromDataList() {
		PropertyDataWithValue<?> newEntry = AddBlockPropertyDialog.showImportDialog(mcreator,
				propertiesList.stream().map(JBlockPropertiesListEntry::getPropertyData).collect(Collectors.toList()),
				nonUserProvidedProperties);
		if (newEntry != null) {
			addPropertiesEntry(newEntry);
		}
	}

	private void addPropertiesEntry(@Nonnull PropertyDataWithValue<?> data) {
		JBlockPropertiesListEntry pe = new JBlockPropertiesListEntry(this, gui, propertyEntries, propertiesList);
		pe.setEntry(data);
		registerEntryUI(pe);

		recalculatePropertiesCap();
	}

	void removeProperty(JBlockPropertiesListEntry entry) {
		propertiesList.remove(entry);
		propertyEntries.remove(entry);
		propertyEntries.revalidate();
		propertyEntries.repaint();

		recalculatePropertiesCap();
	}

	public List<PropertyDataWithValue<?>> getProperties() {
		return propertiesList.stream().map(JBlockPropertiesListEntry::getEntry).collect(Collectors.toList());
	}

	public void setProperties(List<PropertyDataWithValue<?>> properties) {
		properties.forEach(this::addPropertiesEntry);
	}

	public AggregatedValidationResult getValidationResult() {
		AggregatedValidationResult validationResult = new AggregatedValidationResult.PASS();
		if (propertyCombinations > MAX_PROPERTY_COMBINATIONS) {
			validationResult = new AggregatedValidationResult.FAIL(
					L10N.t("elementgui.block.custom_properties.error_too_many_combinations"));
		}
		for (JBlockPropertiesListEntry entry : propertiesList) {
			if (nonUserProvidedProperties.get().contains(
					BlockStatePropertyUtils.propertyRegistryName(entry.getPropertyData()))) {
				entry.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Validator.ValidationResultType.ERROR.getColor(), 1),
						BorderFactory.createEmptyBorder(4, 4, 4, 4)));
				validationResult = new AggregatedValidationResult.FAIL(
						L10N.t("elementgui.block.custom_properties.error_overrides_provided"));
			} else {
				entry.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			}
		}
		return validationResult;
	}

}