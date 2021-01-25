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

/*
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining
 * it with JBoss Forge (or a modified version of that library), containing
 * parts covered by the terms of Eclipse Public License, the licensors of
 * this Program grant you additional permission to convey the resulting work.
 */

package net.mcreator.ui.modgui;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Armor;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.CollapsiblePanel;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.BlockItemTextureSelector;
import net.mcreator.ui.dialogs.JavaModelAnimationEditorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ConditionalTextFieldValidator;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArmorGUI extends ModElementGUI<Armor> {

	private static final Logger LOG = LogManager.getLogger("Armor UI");

	private TextureHolder textureHelmet;
	private TextureHolder textureBody;
	private TextureHolder textureLeggings;
	private TextureHolder textureBoots;

	private final VTextField helmetName = new VTextField();
	private final VTextField bodyName = new VTextField();
	private final VTextField leggingsName = new VTextField();
	private final VTextField bootsName = new VTextField();

	private static final Model defaultModel = new Model.BuiltInModel("Default");
	private final VComboBox<Model> helmetModel = new SearchableComboBox<>();
	private final VComboBox<Model> bodyModel = new SearchableComboBox<>();
	private final VComboBox<Model> leggingsModel = new SearchableComboBox<>();
	private final VComboBox<Model> bootsModel = new SearchableComboBox<>();

	private final JTextField helmetSpecialInfo = new JTextField(20);
	private final JTextField bodySpecialInfo = new JTextField(20);
	private final JTextField leggingsSpecialInfo = new JTextField(20);
	private final JTextField bootsSpecialInfo = new JTextField(20);

	private ActionListener helmetModelListener = null;
	private ActionListener bodyModelListener = null;
	private ActionListener leggingsModelListener = null;
	private ActionListener bootsModelListener = null;

	private final VComboBox<String> helmetModelPart = new SearchableComboBox<>();
	private final VComboBox<String> bodyModelPart = new SearchableComboBox<>();
	private final VComboBox<String> armsModelPartL = new SearchableComboBox<>();
	private final VComboBox<String> armsModelPartR = new SearchableComboBox<>();
	private final VComboBox<String> leggingsModelPartL = new SearchableComboBox<>();
	private final VComboBox<String> leggingsModelPartR = new SearchableComboBox<>();
	private final VComboBox<String> bootsModelPartL = new SearchableComboBox<>();
	private final VComboBox<String> bootsModelPartR = new SearchableComboBox<>();

	private final VComboBox<String> armorTextureFile = new SearchableComboBox<>();

	private final JCheckBox enableHelmet = L10N.checkbox("elementgui.armor.armor_helmet");
	private final JCheckBox enableBody = L10N.checkbox("elementgui.armor.armor_body");
	private final JCheckBox enableLeggings = L10N.checkbox("elementgui.armor.armor_leggings");
	private final JCheckBox enableBoots = L10N.checkbox("elementgui.armor.armor_boots");

	private final VComboBox<String> helmetModelTexture = new SearchableComboBox<>();
	private final VComboBox<String> bodyModelTexture = new SearchableComboBox<>();
	private final VComboBox<String> leggingsModelTexture = new SearchableComboBox<>();
	private final VComboBox<String> bootsModelTexture = new SearchableComboBox<>();

	private final JLabel clo1 = new JLabel();
	private final JLabel clo2 = new JLabel();

	private final SoundSelector equipSound = new SoundSelector(mcreator);

	private final int fact = 5;

	private final JSpinner maxDamage = new JSpinner(new SpinnerNumberModel(25, 0, 1024, 1));
	private final JSpinner damageValueBoots = new JSpinner(new SpinnerNumberModel(2, 0, 1024, 1));
	private final JSpinner damageValueLeggings = new JSpinner(new SpinnerNumberModel(5, 0, 1024, 1));
	private final JSpinner damageValueBody = new JSpinner(new SpinnerNumberModel(6, 0, 1024, 1));
	private final JSpinner damageValueHelmet = new JSpinner(new SpinnerNumberModel(2, 0, 1024, 1));
	private final JSpinner enchantability = new JSpinner(new SpinnerNumberModel(9, 0, 100, 1));
	private final JSpinner toughness = new JSpinner(new SpinnerNumberModel(0.0, 0, 10, 0.1));
	private final JSpinner knockbackResistance = new JSpinner(new SpinnerNumberModel(0.0, 0, 5.0, 0.1));

	private ProcedureSelector onHelmetTick;
	private ProcedureSelector onBodyTick;
	private ProcedureSelector onLeggingsTick;
	private ProcedureSelector onBootsTick;

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private final ValidationGroup group1page = new ValidationGroup();
	private final ValidationGroup group2page = new ValidationGroup();

	private CollapsiblePanel helmetModelPanel;
	private CollapsiblePanel bodyModelPanel;
	private CollapsiblePanel leggingsModelPanel;
	private CollapsiblePanel bootsModelPanel;

	private MCItemListField repairItems;

	public ArmorGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		onHelmetTick = new ProcedureSelector(this.withEntry("armor/helmet_tick"), mcreator,
				L10N.t("elementgui.armor.helmet_tick_event"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onBodyTick = new ProcedureSelector(this.withEntry("armor/body_tick"), mcreator,
				L10N.t("elementgui.armor.chestplate_tick_event"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onLeggingsTick = new ProcedureSelector(this.withEntry("armor/leggings_tick"), mcreator,
				L10N.t("elementgui.armor.leggings_tick_event"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onBootsTick = new ProcedureSelector(this.withEntry("armor/boots_tick"), mcreator,
				L10N.t("elementgui.armor.boots_tick_event"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));

		repairItems = new MCItemListField(mcreator, ElementUtil::loadBlocksAndItems);

		armorTextureFile.setRenderer(new WTextureComboBoxRenderer(element -> {
			File[] armorTextures = mcreator.getFolderManager().getArmorTextureFilesForName(element);
			if (armorTextures[0].isFile() && armorTextures[1].isFile()) {
				return new ImageIcon(armorTextures[0].getAbsolutePath());
			}
			return null;
		}));

		helmetModelTexture.setRenderer(new WTextureComboBoxRenderer.OtherTextures(mcreator.getWorkspace()));
		bodyModelTexture.setRenderer(new WTextureComboBoxRenderer.OtherTextures(mcreator.getWorkspace()));
		leggingsModelTexture.setRenderer(new WTextureComboBoxRenderer.OtherTextures(mcreator.getWorkspace()));
		bootsModelTexture.setRenderer(new WTextureComboBoxRenderer.OtherTextures(mcreator.getWorkspace()));

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane5 = new JPanel(new BorderLayout(10, 10));
		JPanel pane6 = new JPanel(new BorderLayout(10, 10));

		helmetModelTexture.setPreferredSize(new Dimension(180, 36));
		ComponentUtils.deriveFont(helmetModelTexture, 16);

		bodyModelTexture.setPreferredSize(new Dimension(180, 36));
		ComponentUtils.deriveFont(bodyModelTexture, 16);

		leggingsModelTexture.setPreferredSize(new Dimension(180, 36));
		ComponentUtils.deriveFont(leggingsModelTexture, 16);

		bootsModelTexture.setPreferredSize(new Dimension(180, 36));
		ComponentUtils.deriveFont(bootsModelTexture, 16);

		helmetModel.setPreferredSize(new Dimension(200, 36));
		helmetModel.setRenderer(new ModelComboBoxRenderer());
		ComponentUtils.deriveFont(helmetModel, 16);

		helmetModelPart.setPreferredSize(new Dimension(160, 36));
		ComponentUtils.deriveFont(helmetModelPart, 16);

		bodyModel.setPreferredSize(new Dimension(200, 36));
		bodyModel.setRenderer(new ModelComboBoxRenderer());
		ComponentUtils.deriveFont(bodyModel, 16);

		bodyModelPart.setPreferredSize(new Dimension(160, 36));
		ComponentUtils.deriveFont(bodyModelPart, 16);

		leggingsModel.setPreferredSize(new Dimension(200, 36));
		leggingsModel.setRenderer(new ModelComboBoxRenderer());
		ComponentUtils.deriveFont(leggingsModel, 16);

		leggingsModelPartL.setPreferredSize(new Dimension(120, 36));
		ComponentUtils.deriveFont(leggingsModelPartL, 16);
		leggingsModelPartR.setPreferredSize(new Dimension(120, 36));
		ComponentUtils.deriveFont(leggingsModelPartR, 16);

		armsModelPartL.setPreferredSize(new Dimension(120, 36));
		ComponentUtils.deriveFont(armsModelPartL, 16);
		armsModelPartR.setPreferredSize(new Dimension(120, 36));
		ComponentUtils.deriveFont(armsModelPartR, 16);

		bootsModel.setPreferredSize(new Dimension(200, 36));
		bootsModel.setRenderer(new ModelComboBoxRenderer());
		ComponentUtils.deriveFont(bootsModel, 16);

		bootsModelPartL.setPreferredSize(new Dimension(120, 36));
		ComponentUtils.deriveFont(bootsModelPartL, 16);
		bootsModelPartR.setPreferredSize(new Dimension(120, 36));
		ComponentUtils.deriveFont(bootsModelPartR, 16);

		helmetName.setPreferredSize(new Dimension(350, 36));
		bodyName.setPreferredSize(new Dimension(350, 36));
		leggingsName.setPreferredSize(new Dimension(350, 36));
		bootsName.setPreferredSize(new Dimension(350, 36));

		ComponentUtils.deriveFont(helmetName, 16);
		ComponentUtils.deriveFont(bodyName, 16);
		ComponentUtils.deriveFont(leggingsName, 16);
		ComponentUtils.deriveFont(bootsName, 16);

		ComponentUtils.deriveFont(helmetSpecialInfo, 16);
		ComponentUtils.deriveFont(bodySpecialInfo, 16);
		ComponentUtils.deriveFont(leggingsSpecialInfo, 16);
		ComponentUtils.deriveFont(bootsSpecialInfo, 16);

		ComponentUtils.deriveFont(armorTextureFile, 16);

		JPanel destal = new JPanel();
		destal.setLayout(new BoxLayout(destal, BoxLayout.Y_AXIS));
		destal.setOpaque(false);

		textureHelmet = new TextureHolder(
				new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.ITEM));
		textureBody = new TextureHolder(
				new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.ITEM));
		textureLeggings = new TextureHolder(
				new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.ITEM));
		textureBoots = new TextureHolder(
				new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.ITEM));

		textureHelmet.setOpaque(false);
		textureBody.setOpaque(false);
		textureLeggings.setOpaque(false);
		textureBoots.setOpaque(false);

		enableHelmet.setSelected(true);
		enableBody.setSelected(true);
		enableLeggings.setSelected(true);
		enableBoots.setSelected(true);

		enableHelmet.setOpaque(false);
		enableBody.setOpaque(false);
		enableLeggings.setOpaque(false);
		enableBoots.setOpaque(false);

		helmetModelPanel = new CollapsiblePanel(L10N.t("elementgui.armor.advanced_helmet"), PanelUtils
				.northAndCenterElement(PanelUtils
						.join(FlowLayout.LEFT, L10N.label("elementgui.armor.supported_java"), helmetModel,
								new JLabel(":"), helmetModelPart, L10N.label("elementgui.armor.texture"),
								helmetModelTexture), PanelUtils
						.join(FlowLayout.LEFT, L10N.label("elementgui.armor.special_information"), helmetSpecialInfo)));
		helmetModelPanel.toggleVisibility(PreferencesManager.PREFERENCES.ui.expandSectionsByDefault);

		JComponent helText = PanelUtils
				.centerAndSouthElement(PanelUtils.centerInPanelPadding(textureHelmet, 0, 0), enableHelmet);
		helText.setBorder(BorderFactory
				.createCompoundBorder(BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.GRAY_COLOR")),
						BorderFactory.createEmptyBorder(15, 12, 0, 12)));

		destal.add(PanelUtils.westAndCenterElement(helText, PanelUtils.centerAndSouthElement(
				PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.helmet_name"), helmetName),
				helmetModelPanel), 5, 0));

		destal.add(new JEmptyBox(10, 10));

		JComponent bodText = PanelUtils
				.centerAndSouthElement(PanelUtils.centerInPanelPadding(textureBody, 0, 0), enableBody);
		bodText.setBorder(BorderFactory
				.createCompoundBorder(BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.GRAY_COLOR")),
						BorderFactory.createEmptyBorder(15, 17, 0, 17)));

		bodyModelPanel = new CollapsiblePanel(L10N.t("elementgui.armor.advanced_body"), PanelUtils
				.northAndCenterElement(PanelUtils
						.join(FlowLayout.LEFT, L10N.label("elementgui.armor.supported_java"), bodyModel,
								new JLabel(":"), bodyModelPart, L10N.label("elementgui.armor.part_arm_left"),
								armsModelPartL, L10N.label("elementgui.armor.part_arm_right"), armsModelPartR,
								L10N.label("elementgui.armor.texture"), bodyModelTexture), PanelUtils
						.join(FlowLayout.LEFT, L10N.label("elementgui.armor.special_information"), bodySpecialInfo)));
		bodyModelPanel.toggleVisibility(PreferencesManager.PREFERENCES.ui.expandSectionsByDefault);

		destal.add(PanelUtils.westAndCenterElement(bodText, PanelUtils.centerAndSouthElement(
				PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.body_name"), bodyName), bodyModelPanel),
				5, 0));

		destal.add(new JEmptyBox(10, 10));

		JComponent legText = PanelUtils
				.centerAndSouthElement(PanelUtils.centerInPanelPadding(textureLeggings, 0, 0), enableLeggings);
		legText.setBorder(BorderFactory
				.createCompoundBorder(BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.GRAY_COLOR")),
						BorderFactory.createEmptyBorder(15, 8, 0, 8)));

		leggingsModelPanel = new CollapsiblePanel(L10N.t("elementgui.armor.advanced_leggings"), PanelUtils
				.northAndCenterElement(PanelUtils
						.join(FlowLayout.LEFT, L10N.label("elementgui.armor.supported_java"), leggingsModel,
								new JLabel(": L"), leggingsModelPartL, new JLabel("R"), leggingsModelPartR,
								L10N.label("elementgui.armor.texture"), leggingsModelTexture), PanelUtils
						.join(FlowLayout.LEFT, L10N.label("elementgui.armor.special_information"),
								leggingsSpecialInfo)));
		leggingsModelPanel.toggleVisibility(PreferencesManager.PREFERENCES.ui.expandSectionsByDefault);

		destal.add(PanelUtils.westAndCenterElement(legText, PanelUtils.centerAndSouthElement(
				PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.leggings_name"), leggingsName),
				leggingsModelPanel), 5, 0));

		destal.add(new JEmptyBox(10, 10));

		JComponent bootText = PanelUtils
				.centerAndSouthElement(PanelUtils.centerInPanelPadding(textureBoots, 0, 0), enableBoots);
		bootText.setBorder(BorderFactory
				.createCompoundBorder(BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.GRAY_COLOR")),
						BorderFactory.createEmptyBorder(15, 16, 0, 15)));

		bootsModelPanel = new CollapsiblePanel(L10N.t("elementgui.armor.advanced_boots"), PanelUtils
				.northAndCenterElement(PanelUtils
						.join(FlowLayout.LEFT, L10N.label("elementgui.armor.supported_java"), bootsModel,
								new JLabel(": L"), bootsModelPartL, new JLabel("R"), bootsModelPartR,
								L10N.label("elementgui.armor.texture"), bootsModelTexture), PanelUtils
						.join(FlowLayout.LEFT, L10N.label("elementgui.armor.special_information"), bootsSpecialInfo)));
		bootsModelPanel.toggleVisibility(PreferencesManager.PREFERENCES.ui.expandSectionsByDefault);

		destal.add(PanelUtils.westAndCenterElement(bootText, PanelUtils.centerAndSouthElement(
				PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.boots_name"), bootsName),
				bootsModelPanel), 5, 0));

		enableHelmet.addActionListener(event -> {
			textureHelmet.setEnabled(enableHelmet.isSelected());
			helmetName.setEnabled(enableHelmet.isSelected());
		});

		enableBody.addActionListener(event -> {
			textureBody.setEnabled(enableBody.isSelected());
			bodyName.setEnabled(enableBody.isSelected());
		});

		enableLeggings.addActionListener(event -> {
			textureLeggings.setEnabled(enableLeggings.isSelected());
			leggingsName.setEnabled(enableLeggings.isSelected());
		});

		enableBoots.addActionListener(event -> {
			textureBoots.setEnabled(enableBoots.isSelected());
			bootsName.setEnabled(enableBoots.isSelected());
		});

		armorTextureFile.addActionListener(e -> updateArmorTexturePreview());

		JPanel sbbp22 = new JPanel();

		sbbp22.setOpaque(false);

		sbbp22.add(destal);

		GridLayout klo = new GridLayout(2, 2);

		klo.setHgap(20);
		klo.setVgap(20);

		JPanel events = new JPanel();
		events.setLayout(new BoxLayout(events, BoxLayout.PAGE_AXIS));
		JPanel events2 = new JPanel(new GridLayout(1, 4, 8, 8));
		events2.setOpaque(false);
		events2.add(onHelmetTick);
		events2.add(onBodyTick);
		events2.add(onLeggingsTick);
		events2.add(onBootsTick);
		events.add(PanelUtils.join(events2));
		events.setOpaque(false);
		pane6.add("Center", PanelUtils.totalCenterInPanel(events));

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(sbbp22));

		JPanel enderpanel = new JPanel(new GridLayout(9, 2, 20, 10));

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armor/armor_layer_texture"),
				L10N.label("elementgui.armor.layer_texture")));
		enderpanel.add(armorTextureFile);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tab"),
				L10N.label("elementgui.common.creative_tab")));
		enderpanel.add(creativeTab);

		enderpanel.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("armor/equip_sound"), L10N.label("elementgui.armor.equip_sound")));
		enderpanel.add(equipSound);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armor/max_damage_absorbed"),
				L10N.label("elementgui.armor.max_damage_absorption")));
		enderpanel.add(maxDamage);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armor/damage_values"),
				L10N.label("elementgui.armor.damage_values")));
		enderpanel.add(PanelUtils
				.gridElements(1, 4, damageValueHelmet, damageValueBody, damageValueLeggings, damageValueBoots));

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armor/enchantability"),
				L10N.label("elementgui.common.enchantability")));
		enderpanel.add(enchantability);

		enderpanel.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("armor/toughness"), L10N.label("elementgui.armor.toughness")));
		enderpanel.add(toughness);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armor/knockback_resistance"),
				L10N.label("elementgui.armor.knockback_resistance")));
		enderpanel.add(knockbackResistance);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armor/repair_items"),
				L10N.label("elementgui.common.repair_items")));
		enderpanel.add(repairItems);

		enderpanel.setOpaque(false);

		pane5.setOpaque(false);
		pane6.setOpaque(false);

		clo1.setPreferredSize(new Dimension(64 * fact, 32 * fact));
		clo2.setPreferredSize(new Dimension(64 * fact, 32 * fact));

		JPanel clop = new JPanel();
		clop.add(clo1);
		clop.add(clo2);

		clop.setOpaque(false);

		JPanel clopa = new JPanel(new BorderLayout(35, 35));
		clopa.add("Center", enderpanel);
		clopa.add("South", clop);
		clopa.setOpaque(false);

		pane5.add("Center", PanelUtils.totalCenterInPanel(clopa));

		textureHelmet.setValidator(() -> {
			if (enableHelmet.isSelected() && !textureHelmet.has())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.armor.need_texture"));
			return new Validator.ValidationResult(Validator.ValidationResultType.PASSED, "");
		});

		textureBody.setValidator(() -> {
			if (enableBody.isSelected() && !textureBody.has())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.armor.need_texture"));
			return new Validator.ValidationResult(Validator.ValidationResultType.PASSED, "");
		});

		textureLeggings.setValidator(() -> {
			if (enableLeggings.isSelected() && !textureLeggings.has())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.armor.need_texture"));
			return new Validator.ValidationResult(Validator.ValidationResultType.PASSED, "");
		});

		textureBoots.setValidator(() -> {
			if (enableBoots.isSelected() && !textureBoots.has())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.armor.need_texture"));
			return new Validator.ValidationResult(Validator.ValidationResultType.PASSED, "");
		});

		helmetModelListener = actionEvent -> {
			Model model = helmetModel.getSelectedItem();
			if (model != null && model != defaultModel) {
				helmetModelPart.removeAllItems();
				try {
					ComboBoxUtil.updateComboBoxContents(helmetModelPart, new ArrayList<>(JavaModelAnimationEditorDialog
							.getModelParts((JavaClassSource) Roaster.parse(model.getFile()))));
					return;
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}

			helmetModelPart.removeAllItems();
			helmetModelPart.addItem("Helmet");
		};

		bodyModelListener = actionEvent -> {
			Model model = bodyModel.getSelectedItem();
			if (model != null && model != defaultModel) {
				bodyModelPart.removeAllItems();
				armsModelPartL.removeAllItems();
				armsModelPartR.removeAllItems();
				try {
					leggingsModelPartL.addItem("");
					leggingsModelPartR.addItem("");

					ComboBoxUtil.updateComboBoxContents(bodyModelPart, new ArrayList<>(JavaModelAnimationEditorDialog
							.getModelParts((JavaClassSource) Roaster.parse(model.getFile()))));
					ComboBoxUtil.updateComboBoxContents(armsModelPartL, new ArrayList<>(JavaModelAnimationEditorDialog
							.getModelParts((JavaClassSource) Roaster.parse(model.getFile()))));
					ComboBoxUtil.updateComboBoxContents(armsModelPartR, new ArrayList<>(JavaModelAnimationEditorDialog
							.getModelParts((JavaClassSource) Roaster.parse(model.getFile()))));

					return;
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
			bodyModelPart.removeAllItems();
			armsModelPartL.removeAllItems();
			armsModelPartR.removeAllItems();
			bodyModelPart.addItem("Body");
			armsModelPartL.addItem("Arms L");
			armsModelPartR.addItem("Arms R");
		};

		leggingsModelListener = actionEvent -> {
			Model model = leggingsModel.getSelectedItem();
			if (model != null && model != defaultModel) {
				leggingsModelPartL.removeAllItems();
				leggingsModelPartR.removeAllItems();
				try {
					ComboBoxUtil.updateComboBoxContents(leggingsModelPartL, new ArrayList<>(
							JavaModelAnimationEditorDialog
									.getModelParts((JavaClassSource) Roaster.parse(model.getFile()))));
					ComboBoxUtil.updateComboBoxContents(leggingsModelPartR, new ArrayList<>(
							JavaModelAnimationEditorDialog
									.getModelParts((JavaClassSource) Roaster.parse(model.getFile()))));
					return;
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
			leggingsModelPartL.removeAllItems();
			leggingsModelPartR.removeAllItems();
			leggingsModelPartL.addItem("Leggings L");
			leggingsModelPartR.addItem("Leggings R");
		};

		bootsModelListener = actionEvent -> {
			Model model = bootsModel.getSelectedItem();
			if (model != null && model != defaultModel) {
				bootsModelPartL.removeAllItems();
				bootsModelPartR.removeAllItems();
				try {
					ComboBoxUtil.updateComboBoxContents(bootsModelPartL, new ArrayList<>(JavaModelAnimationEditorDialog
							.getModelParts((JavaClassSource) Roaster.parse(model.getFile()))));
					ComboBoxUtil.updateComboBoxContents(bootsModelPartR, new ArrayList<>(JavaModelAnimationEditorDialog
							.getModelParts((JavaClassSource) Roaster.parse(model.getFile()))));
					return;
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
			bootsModelPartL.removeAllItems();
			bootsModelPartR.removeAllItems();
			bootsModelPartL.addItem("Boots L");
			bootsModelPartR.addItem("Boots R");
		};

		helmetModelListener.actionPerformed(new ActionEvent("", 0, ""));
		bodyModelListener.actionPerformed(new ActionEvent("", 0, ""));
		leggingsModelListener.actionPerformed(new ActionEvent("", 0, ""));
		bootsModelListener.actionPerformed(new ActionEvent("", 0, ""));

		bootsName.setValidator(
				new ConditionalTextFieldValidator(bootsName, L10N.t("elementgui.armor.boots_need_name"), enableBoots,
						true));
		bodyName.setValidator(
				new ConditionalTextFieldValidator(bodyName, L10N.t("elementgui.armor.body_needs_name"), enableBody,
						true));
		leggingsName.setValidator(
				new ConditionalTextFieldValidator(leggingsName, L10N.t("elementgui.armor.leggings_need_name"),
						enableLeggings, true));
		helmetName.setValidator(
				new ConditionalTextFieldValidator(helmetName, L10N.t("elementgui.armor.helmet_needs_name"),
						enableHelmet, true));

		bootsName.enableRealtimeValidation();
		bodyName.enableRealtimeValidation();
		leggingsName.enableRealtimeValidation();
		helmetName.enableRealtimeValidation();

		group1page.addValidationElement(textureHelmet);
		group1page.addValidationElement(textureBody);
		group1page.addValidationElement(textureLeggings);
		group1page.addValidationElement(textureBoots);

		group1page.addValidationElement(bootsName);
		group1page.addValidationElement(bodyName);
		group1page.addValidationElement(leggingsName);
		group1page.addValidationElement(helmetName);

		armorTextureFile.setValidator(() -> {
			if (armorTextureFile.getSelectedItem() == null || armorTextureFile.getSelectedItem().equals(""))
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.armor.armor_needs_texture"));
			return new Validator.ValidationResult(Validator.ValidationResultType.PASSED, "");
		});

		group2page.addValidationElement(armorTextureFile);

		addPage(L10N.t("elementgui.common.page_visual"), pane2);
		addPage(L10N.t("elementgui.common.page_properties"), pane5);
		addPage(L10N.t("elementgui.common.page_triggers"), pane6);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			helmetName.setText(L10N.t("elementgui.armor.helmet", readableNameFromModElement));
			bodyName.setText(L10N.t("elementgui.armor.body", readableNameFromModElement));
			leggingsName.setText(L10N.t("elementgui.armor.leggings", readableNameFromModElement));
			bootsName.setText(L10N.t("elementgui.armor.boots", readableNameFromModElement));
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		helmetModel.removeActionListener(helmetModelListener);
		bodyModel.removeActionListener(bodyModelListener);
		leggingsModel.removeActionListener(leggingsModelListener);
		bootsModel.removeActionListener(bootsModelListener);

		onHelmetTick.refreshListKeepSelected();
		onBodyTick.refreshListKeepSelected();
		onLeggingsTick.refreshListKeepSelected();
		onBootsTick.refreshListKeepSelected();
		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("COMBAT"));

		ComboBoxUtil.updateComboBoxContents(helmetModel, ListUtils.merge(Collections.singleton(defaultModel),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(bodyModel, ListUtils.merge(Collections.singleton(defaultModel),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(leggingsModel, ListUtils.merge(Collections.singleton(defaultModel),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(bootsModel, ListUtils.merge(Collections.singleton(defaultModel),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(helmetModelTexture, ListUtils.merge(Collections.singleton("From armor"),
				mcreator.getFolderManager().getOtherTexturesList().stream().map(File::getName)
						.filter(s -> s.endsWith(".png")).collect(Collectors.toList())), "");

		ComboBoxUtil.updateComboBoxContents(bodyModelTexture, ListUtils.merge(Collections.singleton("From armor"),
				mcreator.getFolderManager().getOtherTexturesList().stream().map(File::getName)
						.filter(s -> s.endsWith(".png")).collect(Collectors.toList())), "");

		ComboBoxUtil.updateComboBoxContents(leggingsModelTexture, ListUtils.merge(Collections.singleton("From armor"),
				mcreator.getFolderManager().getOtherTexturesList().stream().map(File::getName)
						.filter(s -> s.endsWith(".png")).collect(Collectors.toList())), "");

		ComboBoxUtil.updateComboBoxContents(bootsModelTexture, ListUtils.merge(Collections.singleton("From armor"),
				mcreator.getFolderManager().getOtherTexturesList().stream().map(File::getName)
						.filter(s -> s.endsWith(".png")).collect(Collectors.toList())), "");

		List<File> armors = mcreator.getFolderManager().getArmorTexturesList();
		List<String> armorPart1s = new ArrayList<>();
		for (File texture : armors)
			if (texture.getName().endsWith("_layer_1.png"))
				armorPart1s.add(texture.getName().replace("_layer_1.png", ""));
		ComboBoxUtil.updateComboBoxContents(armorTextureFile, ListUtils.merge(Collections.singleton(""), armorPart1s));

		helmetModel.addActionListener(helmetModelListener);
		bodyModel.addActionListener(bodyModelListener);
		leggingsModel.addActionListener(leggingsModelListener);
		bootsModel.addActionListener(bootsModelListener);
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 1)
			return new AggregatedValidationResult(group2page);
		else if (page == 0)
			return new AggregatedValidationResult(group1page);
		return new AggregatedValidationResult.PASS();
	}

	private void updateArmorTexturePreview() {
		File[] armorTextures = mcreator.getFolderManager()
				.getArmorTextureFilesForName(armorTextureFile.getSelectedItem());
		if (armorTextures[0].isFile() && armorTextures[1].isFile()) {
			ImageIcon bg1 = new ImageIcon(ImageUtils
					.resize(new ImageIcon(armorTextures[0].getAbsolutePath()).getImage(), 64 * fact, 32 * fact));
			ImageIcon bg2 = new ImageIcon(ImageUtils
					.resize(new ImageIcon(armorTextures[1].getAbsolutePath()).getImage(), 64 * fact, 32 * fact));
			ImageIcon front1 = new ImageIcon(MinecraftImageGenerator.Preview.generateArmorPreviewFrame1());
			ImageIcon front2 = new ImageIcon(MinecraftImageGenerator.Preview.generateArmorPreviewFrame2());
			clo1.setIcon(ImageUtils.drawOver(bg1, front1));
			clo2.setIcon(ImageUtils.drawOver(bg2, front2));
		} else {
			clo1.setIcon(new ImageIcon(MinecraftImageGenerator.Preview.generateArmorPreviewFrame1()));
			clo2.setIcon(new ImageIcon(MinecraftImageGenerator.Preview.generateArmorPreviewFrame2()));
		}
	}

	@Override public void openInEditingMode(Armor armor) {
		textureHelmet.setTextureFromTextureName(armor.textureHelmet);
		textureBody.setTextureFromTextureName(armor.textureBody);
		textureLeggings.setTextureFromTextureName(armor.textureLeggings);
		textureBoots.setTextureFromTextureName(armor.textureBoots);
		armorTextureFile.setSelectedItem(armor.armorTextureFile);
		maxDamage.setValue(armor.maxDamage);
		damageValueBoots.setValue(armor.damageValueBoots);
		damageValueLeggings.setValue(armor.damageValueLeggings);
		damageValueBody.setValue(armor.damageValueBody);
		damageValueHelmet.setValue(armor.damageValueHelmet);
		enchantability.setValue(armor.enchantability);
		toughness.setValue(armor.toughness);
		knockbackResistance.setValue(armor.knockbackResistance);
		onHelmetTick.setSelectedProcedure(armor.onHelmetTick);
		onBodyTick.setSelectedProcedure(armor.onBodyTick);
		onLeggingsTick.setSelectedProcedure(armor.onLeggingsTick);
		onBootsTick.setSelectedProcedure(armor.onBootsTick);
		enableHelmet.setSelected(armor.enableHelmet);
		enableBody.setSelected(armor.enableBody);
		enableLeggings.setSelected(armor.enableLeggings);
		enableBoots.setSelected(armor.enableBoots);
		creativeTab.setSelectedItem(armor.creativeTab);
		textureHelmet.setEnabled(enableHelmet.isSelected());
		textureBody.setEnabled(enableBody.isSelected());
		textureLeggings.setEnabled(enableLeggings.isSelected());
		textureBoots.setEnabled(enableBoots.isSelected());
		helmetName.setText(armor.helmetName);
		bodyName.setText(armor.bodyName);
		leggingsName.setText(armor.leggingsName);
		bootsName.setText(armor.bootsName);
		repairItems.setListElements(armor.repairItems);
		equipSound.setSound(armor.equipSound);

		helmetSpecialInfo.setText(armor.helmetSpecialInfo.stream().map(info -> info.replace(",", "\\,"))
				.collect(Collectors.joining(",")));
		bodySpecialInfo.setText(
				armor.bodySpecialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));
		leggingsSpecialInfo.setText(armor.leggingsSpecialInfo.stream().map(info -> info.replace(",", "\\,"))
				.collect(Collectors.joining(",")));
		bootsSpecialInfo.setText(
				armor.bootsSpecialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));

		Model _helmetModel = armor.getHelmetModel();
		if (_helmetModel != null && _helmetModel.getType() != null && _helmetModel.getReadableName() != null)
			helmetModel.setSelectedItem(_helmetModel);

		Model _bodyModel = armor.getBodyModel();
		if (_bodyModel != null && _bodyModel.getType() != null && _bodyModel.getReadableName() != null)
			bodyModel.setSelectedItem(_bodyModel);

		Model _leggingsModel = armor.getLeggingsModel();
		if (_leggingsModel != null && _leggingsModel.getType() != null && _leggingsModel.getReadableName() != null)
			leggingsModel.setSelectedItem(_leggingsModel);

		Model _bootsModel = armor.getBootsModel();
		if (_bootsModel != null && _bootsModel.getType() != null && _bootsModel.getReadableName() != null)
			bootsModel.setSelectedItem(_bootsModel);

		helmetModelTexture.setSelectedItem(armor.helmetModelTexture);
		bodyModelTexture.setSelectedItem(armor.bodyModelTexture);
		leggingsModelTexture.setSelectedItem(armor.leggingsModelTexture);
		bootsModelTexture.setSelectedItem(armor.bootsModelTexture);

		helmetModelPart.setSelectedItem(armor.helmetModelPart);
		bodyModelPart.setSelectedItem(armor.bodyModelPart);
		armsModelPartL.setSelectedItem(armor.armsModelPartL);
		armsModelPartR.setSelectedItem(armor.armsModelPartR);
		leggingsModelPartL.setSelectedItem(armor.leggingsModelPartL);
		leggingsModelPartR.setSelectedItem(armor.leggingsModelPartR);
		bootsModelPartL.setSelectedItem(armor.bootsModelPartL);
		bootsModelPartR.setSelectedItem(armor.bootsModelPartR);

		helmetModelPanel.toggleVisibility(
				helmetModel.getSelectedItem() != defaultModel || !helmetSpecialInfo.getText().isEmpty());
		bodyModelPanel
				.toggleVisibility(bodyModel.getSelectedItem() != defaultModel || !bodySpecialInfo.getText().isEmpty());
		leggingsModelPanel.toggleVisibility(
				leggingsModel.getSelectedItem() != defaultModel || !leggingsSpecialInfo.getText().isEmpty());
		bootsModelPanel.toggleVisibility(
				bootsModel.getSelectedItem() != defaultModel || !bootsSpecialInfo.getText().isEmpty());

		updateArmorTexturePreview();
	}

	@Override public Armor getElementFromGUI() {
		Armor armor = new Armor(modElement);
		armor.enableHelmet = enableHelmet.isSelected();
		armor.textureHelmet = textureHelmet.getID();
		armor.enableBody = enableBody.isSelected();
		armor.textureBody = textureBody.getID();
		armor.enableLeggings = enableLeggings.isSelected();
		armor.textureLeggings = textureLeggings.getID();
		armor.enableBoots = enableBoots.isSelected();
		armor.textureBoots = textureBoots.getID();
		armor.onHelmetTick = onHelmetTick.getSelectedProcedure();
		armor.onBodyTick = onBodyTick.getSelectedProcedure();
		armor.onLeggingsTick = onLeggingsTick.getSelectedProcedure();
		armor.onBootsTick = onBootsTick.getSelectedProcedure();
		armor.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		armor.armorTextureFile = armorTextureFile.getSelectedItem();
		armor.maxDamage = (int) maxDamage.getValue();
		armor.damageValueHelmet = (int) damageValueHelmet.getValue();
		armor.damageValueBody = (int) damageValueBody.getValue();
		armor.damageValueLeggings = (int) damageValueLeggings.getValue();
		armor.damageValueBoots = (int) damageValueBoots.getValue();
		armor.enchantability = (int) enchantability.getValue();
		armor.toughness = (double) toughness.getValue();
		armor.knockbackResistance = (double) knockbackResistance.getValue();
		armor.helmetName = helmetName.getText();
		armor.bodyName = bodyName.getText();
		armor.leggingsName = leggingsName.getText();
		armor.bootsName = bootsName.getText();
		armor.helmetModelName = (Objects.requireNonNull(helmetModel.getSelectedItem())).getReadableName();
		armor.bodyModelName = (Objects.requireNonNull(bodyModel.getSelectedItem())).getReadableName();
		armor.leggingsModelName = (Objects.requireNonNull(leggingsModel.getSelectedItem())).getReadableName();
		armor.bootsModelName = (Objects.requireNonNull(bootsModel.getSelectedItem())).getReadableName();
		armor.helmetModelPart = helmetModelPart.getSelectedItem();
		armor.bodyModelPart = bodyModelPart.getSelectedItem();
		armor.armsModelPartL = armsModelPartL.getSelectedItem();
		armor.armsModelPartR = armsModelPartR.getSelectedItem();
		armor.leggingsModelPartL = leggingsModelPartL.getSelectedItem();
		armor.leggingsModelPartR = leggingsModelPartR.getSelectedItem();
		armor.bootsModelPartL = bootsModelPartL.getSelectedItem();
		armor.bootsModelPartR = bootsModelPartR.getSelectedItem();
		armor.helmetModelTexture = helmetModelTexture.getSelectedItem();
		armor.bodyModelTexture = bodyModelTexture.getSelectedItem();
		armor.leggingsModelTexture = leggingsModelTexture.getSelectedItem();
		armor.bootsModelTexture = bootsModelTexture.getSelectedItem();
		armor.equipSound = equipSound.getSound();
		armor.repairItems = repairItems.getListElements();
		armor.helmetSpecialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(helmetSpecialInfo.getText());
		armor.bodySpecialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(bodySpecialInfo.getText());
		armor.leggingsSpecialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(leggingsSpecialInfo.getText());
		armor.bootsSpecialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(bootsSpecialInfo.getText());
		return armor;
	}

	@Override protected void afterGeneratableElementStored() {
		super.afterGeneratableElementStored();
		modElement.clearMetadata();
		modElement.putMetadata("eh", enableHelmet.isSelected());
		modElement.putMetadata("ec", enableBody.isSelected());
		modElement.putMetadata("el", enableLeggings.isSelected());
		modElement.putMetadata("eb", enableBoots.isSelected());
		modElement.reinit();
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-armor");
	}

}
