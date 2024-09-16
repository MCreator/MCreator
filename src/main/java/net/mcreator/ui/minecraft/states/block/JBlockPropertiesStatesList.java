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
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.TechnicalButton;
import net.mcreator.ui.component.entries.JEntriesList;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JBlockPropertiesStatesList extends JEntriesList {

	private final Supplier<Collection<String>> nonUserProvidedProperties;

	private final List<JBlockPropertiesListEntry> propertiesList = new ArrayList<>();

	private final JPanel propertyEntries = new JPanel();

	private final TechnicalButton addProperty = new TechnicalButton(UIRES.get("16px.add"));

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

		JPanel mainContent = new JPanel(new BorderLayout());
		mainContent.setOpaque(false);
		mainContent.add("North", PanelUtils.join(FlowLayout.LEFT, 0, 5, addProperty, new JEmptyBox(5, 5),
				HelpUtils.helpButton(gui.withEntry("block/block_states"))));
		mainContent.add("Center", scrollProperties);

		JPanel basePane = new JPanel(new GridLayout());
		basePane.setOpaque(false);
		basePane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.custom_properties_states"), 0, 0, basePane.getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));
		basePane.add(mainContent);
		add("Center", basePane);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		addProperty.setEnabled(enabled);

		propertiesList.forEach(e -> e.setEnabled(enabled));

		propertyEntries.setEnabled(enabled);
	}

	private void createPropertiesEntry() {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("elementgui.block.custom_properties.add.title"),
				true);

		VTextField name = new VTextField(20);
		name.setValidator(new UniqueNameValidator(L10N.t("elementgui.block.custom_properties.add.input"), name::getText,
				() -> propertiesList.stream().map(e -> e.getPropertyData().getName()), nonUserProvidedProperties.get(),
				new RegistryNameValidator(name, L10N.t("elementgui.block.custom_properties.add.input"))));
		name.enableRealtimeValidation();
		JComboBox<String> type = new JComboBox<>(new String[] { "Logic", "Integer" });

		JMinMaxSpinner integerBounds = new JMinMaxSpinner(0, 1, 0, Integer.MAX_VALUE, 1);

		CardLayout cards = new CardLayout();
		JPanel bounds = new JPanel(cards);
		bounds.setPreferredSize(new Dimension(0, 28));
		bounds.add("Logic", new JEmptyBox());
		bounds.add("Integer", PanelUtils.gridElements(1, 0, 2, 0, L10N.label("elementgui.block.custom_property.values"),
				integerBounds));
		type.addActionListener(e -> cards.show(bounds, (String) type.getSelectedItem()));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		dialog.getRootPane().setDefaultButton(ok);

		ok.addActionListener(e -> {
			if (name.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				String propertyName = "CUSTOM:" + name.getText();
				if ("Logic".equals(type.getSelectedItem())) {
					addPropertiesEntry(new PropertyDataWithValue<>(new PropertyData.LogicType(propertyName), null));
					dialog.setVisible(false);
				} else if ("Integer".equals(type.getSelectedItem())) {
					addPropertiesEntry(new PropertyDataWithValue<>(
							new PropertyData.IntegerType(propertyName, integerBounds.getIntMinValue(),
									integerBounds.getIntMaxValue()), null));
					dialog.setVisible(false);
				}
			}
		});
		cancel.addActionListener(e -> dialog.setVisible(false));

		JPanel main = new JPanel(new GridLayout(0, 1, 0, 2));
		main.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		main.add(PanelUtils.gridElements(1, 0, 2, 0, L10N.label("elementgui.block.custom_property.name"), name));
		main.add(PanelUtils.gridElements(1, 0, 2, 0, L10N.label("elementgui.block.custom_property.type"), type));
		main.add(bounds);

		dialog.getContentPane().add("Center", main);
		dialog.getContentPane().add("South", PanelUtils.join(ok, cancel));
		dialog.pack();
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);
	}

	private void addPropertiesEntry(@Nonnull PropertyDataWithValue<?> data) {
		JBlockPropertiesListEntry pe = new JBlockPropertiesListEntry(this, gui, propertyEntries, propertiesList);
		pe.setEntry(data);
		registerEntryUI(pe);
	}

	void removeProperty(JBlockPropertiesListEntry entry) {
		propertiesList.remove(entry);
		propertyEntries.remove(entry);
		propertyEntries.revalidate();
		propertyEntries.repaint();
	}

	public List<PropertyDataWithValue<?>> getProperties() {
		return propertiesList.stream().map(JBlockPropertiesListEntry::getEntry).collect(Collectors.toList());
	}

	public void setProperties(List<PropertyDataWithValue<?>> properties) {
		properties.forEach(this::addPropertiesEntry);
	}

	public AggregatedValidationResult getValidationResult() {
		AggregatedValidationResult validationResult = new AggregatedValidationResult.PASS();
		for (JBlockPropertiesListEntry entry : propertiesList) {
			if (nonUserProvidedProperties.get().contains(entry.getPropertyData().getName().replace("CUSTOM:", ""))) {
				entry.setBorder(
						BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(204, 108, 108), 1),
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