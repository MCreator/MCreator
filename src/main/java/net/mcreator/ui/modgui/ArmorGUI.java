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
import net.mcreator.minecraft.JavaModels;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.CollapsiblePanel;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.minecraft.SoundSelector;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.LogicProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.procedure.StringListProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ConditionalTextFieldValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ArmorGUI extends ModElementGUI<Armor> {

	private static final Logger LOG = LogManager.getLogger("Armor UI");

	private static final int ARMOR_TEXTURE_SIZE_FACTOR = 5;

	private TextureHolder textureHelmet;
	private TextureHolder textureBody;
	private TextureHolder textureLeggings;
	private TextureHolder textureBoots;

	private final VTextField helmetName = new VTextField();
	private final VTextField bodyName = new VTextField();
	private final VTextField leggingsName = new VTextField();
	private final VTextField bootsName = new VTextField();

	private static final Model defaultModel = new Model.BuiltInModel("Default");
	private final VComboBox<Model> helmetModel = new SearchableComboBox<>(new Model[] { defaultModel });
	private final VComboBox<Model> bodyModel = new SearchableComboBox<>(new Model[] { defaultModel });
	private final VComboBox<Model> leggingsModel = new SearchableComboBox<>(new Model[] { defaultModel });
	private final VComboBox<Model> bootsModel = new SearchableComboBox<>(new Model[] { defaultModel });

	private StringListProcedureSelector helmetSpecialInformation;
	private StringListProcedureSelector bodySpecialInformation;
	private StringListProcedureSelector leggingsSpecialInformation;
	private StringListProcedureSelector bootsSpecialInformation;

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
	private final JCheckBox enableBody = L10N.checkbox("elementgui.armor.armor_chestplate");
	private final JCheckBox enableLeggings = L10N.checkbox("elementgui.armor.armor_leggings");
	private final JCheckBox enableBoots = L10N.checkbox("elementgui.armor.armor_boots");

	private final VComboBox<String> helmetModelTexture = new SearchableComboBox<>();
	private final VComboBox<String> bodyModelTexture = new SearchableComboBox<>();
	private final VComboBox<String> leggingsModelTexture = new SearchableComboBox<>();
	private final VComboBox<String> bootsModelTexture = new SearchableComboBox<>();

	private final Model normal = new Model.BuiltInModel("Normal");
	private final Model tool = new Model.BuiltInModel("Tool");
	private final SearchableComboBox<Model> helmetItemRenderType = new SearchableComboBox<>(
			new Model[] { normal, tool });
	private final SearchableComboBox<Model> bodyItemRenderType = new SearchableComboBox<>(new Model[] { normal, tool });
	private final SearchableComboBox<Model> leggingsItemRenderType = new SearchableComboBox<>(
			new Model[] { normal, tool });
	private final SearchableComboBox<Model> bootsItemRenderType = new SearchableComboBox<>(
			new Model[] { normal, tool });

	private final JCheckBox helmetImmuneToFire = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox bodyImmuneToFire = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox leggingsImmuneToFire = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox bootsImmuneToFire = L10N.checkbox("elementgui.common.enable");

	private LogicProcedureSelector helmetPiglinNeutral;
	private LogicProcedureSelector bodyPiglinNeutral;
	private LogicProcedureSelector leggingsPiglinNeutral;
	private LogicProcedureSelector bootsPiglinNeutral;

	private final JLabel clo1 = new JLabel();
	private final JLabel clo2 = new JLabel();

	private final SoundSelector equipSound = new SoundSelector(mcreator);

	private final JSpinner maxDamage = new JSpinner(new SpinnerNumberModel(15, 0, 1024, 1));
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

	private CollapsiblePanel helmetCollapsiblePanel;
	private CollapsiblePanel bodyCollapsiblePanel;
	private CollapsiblePanel leggingsCollapsiblePanel;
	private CollapsiblePanel bootsCollapsiblePanel;

	private MCItemListField repairItems;

	public ArmorGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		helmetPiglinNeutral = new LogicProcedureSelector(this.withEntry("armor/piglin_neutral"), mcreator,
				L10N.t("elementgui.armor.piglin_neutral"), AbstractProcedureSelector.Side.BOTH,
				L10N.checkbox("elementgui.common.enable"), 0,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		bodyPiglinNeutral = new LogicProcedureSelector(this.withEntry("armor/piglin_neutral"), mcreator,
				L10N.t("elementgui.armor.piglin_neutral"), AbstractProcedureSelector.Side.BOTH,
				L10N.checkbox("elementgui.common.enable"), 0,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		leggingsPiglinNeutral = new LogicProcedureSelector(this.withEntry("armor/piglin_neutral"), mcreator,
				L10N.t("elementgui.armor.piglin_neutral"), AbstractProcedureSelector.Side.BOTH,
				L10N.checkbox("elementgui.common.enable"), 0,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		bootsPiglinNeutral = new LogicProcedureSelector(this.withEntry("armor/piglin_neutral"), mcreator,
				L10N.t("elementgui.armor.piglin_neutral"), AbstractProcedureSelector.Side.BOTH,
				L10N.checkbox("elementgui.common.enable"), 0,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
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

		helmetSpecialInformation = new StringListProcedureSelector(this.withEntry("item/special_information"), mcreator,
				L10N.t("elementgui.common.special_information"), AbstractProcedureSelector.Side.CLIENT,
				new JStringListField(mcreator, null), 0,
				Dependency.fromString("x:number/y:number/z:number/entity:entity/world:world/itemstack:itemstack"));
		bodySpecialInformation = new StringListProcedureSelector(this.withEntry("item/special_information"), mcreator,
				L10N.t("elementgui.common.special_information"), AbstractProcedureSelector.Side.CLIENT,
				new JStringListField(mcreator, null), 0,
				Dependency.fromString("x:number/y:number/z:number/entity:entity/world:world/itemstack:itemstack"));
		leggingsSpecialInformation = new StringListProcedureSelector(this.withEntry("item/special_information"),
				mcreator, L10N.t("elementgui.common.special_information"), AbstractProcedureSelector.Side.CLIENT,
				new JStringListField(mcreator, null), 0,
				Dependency.fromString("x:number/y:number/z:number/entity:entity/world:world/itemstack:itemstack"));
		bootsSpecialInformation = new StringListProcedureSelector(this.withEntry("item/special_information"), mcreator,
				L10N.t("elementgui.common.special_information"), AbstractProcedureSelector.Side.CLIENT,
				new JStringListField(mcreator, null), 0,
				Dependency.fromString("x:number/y:number/z:number/entity:entity/world:world/itemstack:itemstack"));

		repairItems = new MCItemListField(mcreator, ElementUtil::loadBlocksAndItemsAndTags, false, true);

		armorTextureFile.setRenderer(new WTextureComboBoxRenderer(element -> {
			File[] armorTextures = mcreator.getFolderManager().getArmorTextureFilesForName(element);
			if (armorTextures[0].isFile() && armorTextures[1].isFile()) {
				return new ImageIcon(armorTextures[0].getAbsolutePath());
			}
			return null;
		}));

		helmetModelTexture.setRenderer(
				new WTextureComboBoxRenderer.TypeTextures(mcreator.getWorkspace(), TextureType.ENTITY));
		bodyModelTexture.setRenderer(
				new WTextureComboBoxRenderer.TypeTextures(mcreator.getWorkspace(), TextureType.ENTITY));
		leggingsModelTexture.setRenderer(
				new WTextureComboBoxRenderer.TypeTextures(mcreator.getWorkspace(), TextureType.ENTITY));
		bootsModelTexture.setRenderer(
				new WTextureComboBoxRenderer.TypeTextures(mcreator.getWorkspace(), TextureType.ENTITY));

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane5 = new JPanel(new BorderLayout(10, 10));
		JPanel pane6 = new JPanel(new BorderLayout(10, 10));

		ComponentUtils.deriveFont(helmetModelTexture, 16);
		ComponentUtils.deriveFont(bodyModelTexture, 16);
		ComponentUtils.deriveFont(leggingsModelTexture, 16);
		ComponentUtils.deriveFont(bootsModelTexture, 16);

		helmetModel.setRenderer(new ModelComboBoxRenderer());
		ComponentUtils.deriveFont(helmetModel, 16);
		ComponentUtils.deriveFont(helmetModelPart, 16);

		bodyModel.setRenderer(new ModelComboBoxRenderer());
		ComponentUtils.deriveFont(bodyModel, 16);
		ComponentUtils.deriveFont(bodyModelPart, 16);

		leggingsModel.setRenderer(new ModelComboBoxRenderer());
		ComponentUtils.deriveFont(leggingsModel, 16);
		ComponentUtils.deriveFont(leggingsModelPartL, 16);
		ComponentUtils.deriveFont(leggingsModelPartR, 16);

		ComponentUtils.deriveFont(armsModelPartL, 16);
		ComponentUtils.deriveFont(armsModelPartR, 16);

		bootsModel.setRenderer(new ModelComboBoxRenderer());
		ComponentUtils.deriveFont(bootsModel, 16);
		ComponentUtils.deriveFont(bootsModelPartL, 16);
		ComponentUtils.deriveFont(bootsModelPartR, 16);

		helmetName.setPreferredSize(new Dimension(350, 36));
		bodyName.setPreferredSize(new Dimension(350, 36));
		leggingsName.setPreferredSize(new Dimension(350, 36));
		bootsName.setPreferredSize(new Dimension(350, 36));

		ComponentUtils.deriveFont(helmetItemRenderType, 16);
		helmetItemRenderType.setRenderer(new ModelComboBoxRenderer());

		ComponentUtils.deriveFont(bodyItemRenderType, 16);
		bodyItemRenderType.setRenderer(new ModelComboBoxRenderer());

		ComponentUtils.deriveFont(leggingsItemRenderType, 16);
		leggingsItemRenderType.setRenderer(new ModelComboBoxRenderer());

		ComponentUtils.deriveFont(bootsItemRenderType, 16);
		bootsItemRenderType.setRenderer(new ModelComboBoxRenderer());

		ComponentUtils.deriveFont(helmetName, 16);
		ComponentUtils.deriveFont(bodyName, 16);
		ComponentUtils.deriveFont(leggingsName, 16);
		ComponentUtils.deriveFont(bootsName, 16);

		ComponentUtils.deriveFont(armorTextureFile, 16);

		JPanel destal = new JPanel();
		destal.setLayout(new BoxLayout(destal, BoxLayout.Y_AXIS));
		destal.setOpaque(false);

		textureHelmet = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM));
		textureBody = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM));
		textureLeggings = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM));
		textureBoots = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM));

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

		helmetImmuneToFire.setOpaque(false);
		bodyImmuneToFire.setOpaque(false);
		leggingsImmuneToFire.setOpaque(false);
		bootsImmuneToFire.setOpaque(false);

		JPanel helmetSubPanel = new JPanel(new GridLayout(4, 2, 2, 2));
		helmetSubPanel.setOpaque(false);

		helmetSubPanel.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.supported_java")));
		helmetSubPanel.add(PanelUtils.gridElements(1, 2, 2, 2, helmetModel, helmetModelPart));

		helmetSubPanel.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.texture")));
		helmetSubPanel.add(helmetModelTexture);

		helmetSubPanel.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/model"), L10N.label("elementgui.common.item_model")));
		helmetSubPanel.add(helmetItemRenderType);

		helmetSubPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/immune_to_fire"),
				L10N.label("elementgui.item.is_immune_to_fire")));
		helmetSubPanel.add(helmetImmuneToFire);

		helmetCollapsiblePanel = new CollapsiblePanel(L10N.t("elementgui.armor.advanced_helmet"),
				PanelUtils.northAndCenterElement(helmetSubPanel,
						PanelUtils.centerAndSouthElement(helmetPiglinNeutral, helmetSpecialInformation, 2, 2)));

		JComponent helText = PanelUtils.centerAndSouthElement(PanelUtils.centerInPanelPadding(textureHelmet, 0, 0),
				enableHelmet);
		helText.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Theme.current().getAltForegroundColor()),
				BorderFactory.createEmptyBorder(10, 0, 0, 0)));
		helText.setPreferredSize(new Dimension(145, 110));

		destal.add(PanelUtils.westAndCenterElement(PanelUtils.pullElementUp(helText), PanelUtils.centerAndSouthElement(
				PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.helmet_name"), helmetName),
				helmetCollapsiblePanel), 5, 0));

		destal.add(new JEmptyBox(10, 10));

		JComponent bodText = PanelUtils.centerAndSouthElement(PanelUtils.centerInPanelPadding(textureBody, 0, 0),
				enableBody);
		bodText.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Theme.current().getAltForegroundColor()),
				BorderFactory.createEmptyBorder(10, 0, 0, 0)));
		bodText.setPreferredSize(new Dimension(145, 110));

		JPanel bodySubPanel = new JPanel(new GridLayout(5, 2, 2, 2));
		bodySubPanel.setOpaque(false);

		bodySubPanel.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.supported_java")));
		bodySubPanel.add(PanelUtils.gridElements(1, 2, 2, 2, bodyModel, bodyModelPart));

		bodySubPanel.add(new JEmptyBox());
		bodySubPanel.add(PanelUtils.gridElements(1, 2, 2, 2,
				PanelUtils.westAndCenterElement(L10N.label("elementgui.armor.part_arm_left"), armsModelPartL, 5, 5),
				PanelUtils.westAndCenterElement(L10N.label("elementgui.armor.part_arm_right"), armsModelPartR, 5, 5)));

		bodySubPanel.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.texture")));
		bodySubPanel.add(bodyModelTexture);

		bodySubPanel.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/model"), L10N.label("elementgui.common.item_model")));
		bodySubPanel.add(bodyItemRenderType);

		bodySubPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/immune_to_fire"),
				L10N.label("elementgui.item.is_immune_to_fire")));
		bodySubPanel.add(bodyImmuneToFire);

		bodyCollapsiblePanel = new CollapsiblePanel(L10N.t("elementgui.armor.advanced_chestplate"),
				PanelUtils.centerAndSouthElement(bodySubPanel,
						PanelUtils.centerAndSouthElement(bodyPiglinNeutral, bodySpecialInformation, 2, 2)));

		destal.add(PanelUtils.westAndCenterElement(PanelUtils.pullElementUp(bodText), PanelUtils.centerAndSouthElement(
				PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.chestplate_name"), bodyName),
				bodyCollapsiblePanel), 5, 0));

		destal.add(new JEmptyBox(10, 10));

		JComponent legText = PanelUtils.centerAndSouthElement(PanelUtils.centerInPanelPadding(textureLeggings, 0, 0),
				enableLeggings);
		legText.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Theme.current().getAltForegroundColor()),
				BorderFactory.createEmptyBorder(10, 0, 0, 0)));
		legText.setPreferredSize(new Dimension(145, 110));

		JPanel leggingsSubPanel = new JPanel(new GridLayout(5, 2, 2, 2));
		leggingsSubPanel.setOpaque(false);

		leggingsSubPanel.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.supported_java")));
		leggingsSubPanel.add(leggingsModel);

		leggingsSubPanel.add(new JEmptyBox());
		leggingsSubPanel.add(PanelUtils.gridElements(1, 2, 2, 2,
				PanelUtils.westAndCenterElement(L10N.label("elementgui.armor.part_leg_left"), leggingsModelPartL, 5, 5),
				PanelUtils.westAndCenterElement(L10N.label("elementgui.armor.part_leg_right"), leggingsModelPartR, 5,
						5)));

		leggingsSubPanel.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.texture")));
		leggingsSubPanel.add(leggingsModelTexture);

		leggingsSubPanel.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/model"), L10N.label("elementgui.common.item_model")));
		leggingsSubPanel.add(leggingsItemRenderType);

		leggingsSubPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/immune_to_fire"),
				L10N.label("elementgui.item.is_immune_to_fire")));
		leggingsSubPanel.add(leggingsImmuneToFire);

		leggingsCollapsiblePanel = new CollapsiblePanel(L10N.t("elementgui.armor.advanced_leggings"),
				PanelUtils.centerAndSouthElement(leggingsSubPanel,
						PanelUtils.centerAndSouthElement(leggingsPiglinNeutral, leggingsSpecialInformation, 2, 2)));

		destal.add(PanelUtils.westAndCenterElement(PanelUtils.pullElementUp(legText), PanelUtils.centerAndSouthElement(
				PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.leggings_name"), leggingsName),
				leggingsCollapsiblePanel), 5, 0));

		destal.add(new JEmptyBox(10, 10));

		JComponent bootText = PanelUtils.centerAndSouthElement(PanelUtils.centerInPanelPadding(textureBoots, 0, 0),
				enableBoots);
		bootText.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Theme.current().getAltForegroundColor()),
				BorderFactory.createEmptyBorder(10, 0, 0, 0)));
		bootText.setPreferredSize(new Dimension(145, 110));

		JPanel bootsSubPanel = new JPanel(new GridLayout(5, 2, 2, 2));
		bootsSubPanel.setOpaque(false);

		bootsSubPanel.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.supported_java")));
		bootsSubPanel.add(bootsModel);

		bootsSubPanel.add(new JEmptyBox());
		bootsSubPanel.add(PanelUtils.gridElements(1, 2, 2, 2,
				PanelUtils.westAndCenterElement(L10N.label("elementgui.armor.part_boot_left"), bootsModelPartL, 5, 5),
				PanelUtils.westAndCenterElement(L10N.label("elementgui.armor.part_boot_right"), bootsModelPartR, 5,
						5)));

		bootsSubPanel.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.texture")));
		bootsSubPanel.add(bootsModelTexture);

		bootsSubPanel.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/model"), L10N.label("elementgui.common.item_model")));
		bootsSubPanel.add(bootsItemRenderType);

		bootsSubPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/immune_to_fire"),
				L10N.label("elementgui.item.is_immune_to_fire")));
		bootsSubPanel.add(bootsImmuneToFire);

		bootsCollapsiblePanel = new CollapsiblePanel(L10N.t("elementgui.armor.advanced_boots"),
				PanelUtils.centerAndSouthElement(bootsSubPanel,
						PanelUtils.centerAndSouthElement(bootsPiglinNeutral, bootsSpecialInformation, 2, 2)));

		destal.add(PanelUtils.westAndCenterElement(PanelUtils.pullElementUp(bootText), PanelUtils.centerAndSouthElement(
				PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.armor.boots_name"), bootsName),
				bootsCollapsiblePanel), 5, 0));

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

		JPanel events = new JPanel(new GridLayout(1, 4, 5, 5));
		events.setOpaque(false);
		events.add(onHelmetTick);
		events.add(onBodyTick);
		events.add(onLeggingsTick);
		events.add(onBootsTick);
		pane6.add("Center", PanelUtils.totalCenterInPanel(events));

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(sbbp22));

		JPanel enderpanel = new JPanel(new GridLayout(9, 2, 20, 2));

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armor/armor_layer_texture"),
				L10N.label("elementgui.armor.layer_texture")));
		enderpanel.add(armorTextureFile);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tab"),
				L10N.label("elementgui.common.creative_tab")));
		enderpanel.add(creativeTab);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armor/equip_sound"),
				L10N.label("elementgui.armor.equip_sound")));
		enderpanel.add(equipSound);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armor/max_damage_absorbed"),
				L10N.label("elementgui.armor.max_damage_absorption")));
		enderpanel.add(maxDamage);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armor/damage_values"),
				L10N.label("elementgui.armor.damage_values")));
		enderpanel.add(PanelUtils.gridElements(1, 4, 2, 2, damageValueHelmet, damageValueBody, damageValueLeggings,
				damageValueBoots));

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armor/enchantability"),
				L10N.label("elementgui.common.enchantability")));
		enderpanel.add(enchantability);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("armor/toughness"),
				L10N.label("elementgui.armor.toughness")));
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

		clo1.setPreferredSize(new Dimension(64 * ARMOR_TEXTURE_SIZE_FACTOR, 32 * ARMOR_TEXTURE_SIZE_FACTOR));
		clo2.setPreferredSize(new Dimension(64 * ARMOR_TEXTURE_SIZE_FACTOR, 32 * ARMOR_TEXTURE_SIZE_FACTOR));

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
			if (enableHelmet.isSelected() && !textureHelmet.hasTexture())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.armor.need_texture"));
			return Validator.ValidationResult.PASSED;
		});

		textureBody.setValidator(() -> {
			if (enableBody.isSelected() && !textureBody.hasTexture())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.armor.need_texture"));
			return Validator.ValidationResult.PASSED;
		});

		textureLeggings.setValidator(() -> {
			if (enableLeggings.isSelected() && !textureLeggings.hasTexture())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.armor.need_texture"));
			return Validator.ValidationResult.PASSED;
		});

		textureBoots.setValidator(() -> {
			if (enableBoots.isSelected() && !textureBoots.hasTexture())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.armor.need_texture"));
			return Validator.ValidationResult.PASSED;
		});

		helmetModelListener = actionEvent -> {
			Model model = helmetModel.getSelectedItem();
			if (model != null && model != defaultModel) {
				helmetModelPart.removeAllItems();
				try {
					ComboBoxUtil.updateComboBoxContents(helmetModelPart,
							JavaModels.getModelParts((JavaClassSource) Roaster.parse(model.getFile())));
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

					ComboBoxUtil.updateComboBoxContents(bodyModelPart,
							JavaModels.getModelParts((JavaClassSource) Roaster.parse(model.getFile())));
					ComboBoxUtil.updateComboBoxContents(armsModelPartL,
							JavaModels.getModelParts((JavaClassSource) Roaster.parse(model.getFile())));
					ComboBoxUtil.updateComboBoxContents(armsModelPartR,
							JavaModels.getModelParts((JavaClassSource) Roaster.parse(model.getFile())));

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
					ComboBoxUtil.updateComboBoxContents(leggingsModelPartL,
							JavaModels.getModelParts((JavaClassSource) Roaster.parse(model.getFile())));
					ComboBoxUtil.updateComboBoxContents(leggingsModelPartR,
							JavaModels.getModelParts((JavaClassSource) Roaster.parse(model.getFile())));
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
					ComboBoxUtil.updateComboBoxContents(bootsModelPartL,
							JavaModels.getModelParts((JavaClassSource) Roaster.parse(model.getFile())));
					ComboBoxUtil.updateComboBoxContents(bootsModelPartR,
							JavaModels.getModelParts((JavaClassSource) Roaster.parse(model.getFile())));
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
				new ConditionalTextFieldValidator(bodyName, L10N.t("elementgui.armor.chestplate_needs_name"),
						enableBody, true));
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
			if (armorTextureFile.getSelectedItem() == null || armorTextureFile.getSelectedItem().isEmpty())
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.armor.armor_needs_texture"));
			return Validator.ValidationResult.PASSED;
		});

		group2page.addValidationElement(armorTextureFile);

		addPage(L10N.t("elementgui.common.page_visual"), pane2);
		addPage(L10N.t("elementgui.common.page_properties"), pane5);
		addPage(L10N.t("elementgui.common.page_triggers"), pane6);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			helmetName.setText(L10N.t("elementgui.armor.helmet", readableNameFromModElement));
			bodyName.setText(L10N.t("elementgui.armor.chestplate", readableNameFromModElement));
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

		helmetPiglinNeutral.refreshListKeepSelected();
		bodyPiglinNeutral.refreshListKeepSelected();
		leggingsPiglinNeutral.refreshListKeepSelected();
		bootsPiglinNeutral.refreshListKeepSelected();

		onHelmetTick.refreshListKeepSelected();
		onBodyTick.refreshListKeepSelected();
		onLeggingsTick.refreshListKeepSelected();
		onBootsTick.refreshListKeepSelected();
		helmetSpecialInformation.refreshListKeepSelected();
		bodySpecialInformation.refreshListKeepSelected();
		leggingsSpecialInformation.refreshListKeepSelected();
		bootsSpecialInformation.refreshListKeepSelected();
		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("COMBAT"));

		ComboBoxUtil.updateComboBoxContents(helmetModel, ListUtils.merge(Collections.singleton(defaultModel),
				Model.getModels(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(bodyModel, ListUtils.merge(Collections.singleton(defaultModel),
				Model.getModels(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(leggingsModel, ListUtils.merge(Collections.singleton(defaultModel),
				Model.getModels(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(bootsModel, ListUtils.merge(Collections.singleton(defaultModel),
				Model.getModels(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JAVA || el.getType() == Model.Type.MCREATOR)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(helmetModelTexture, ListUtils.merge(Collections.singleton("From armor"),
				mcreator.getFolderManager().getTexturesList(TextureType.ENTITY).stream().map(File::getName)
						.filter(s -> s.endsWith(".png")).collect(Collectors.toList())), "");

		ComboBoxUtil.updateComboBoxContents(bodyModelTexture, ListUtils.merge(Collections.singleton("From armor"),
				mcreator.getFolderManager().getTexturesList(TextureType.ENTITY).stream().map(File::getName)
						.filter(s -> s.endsWith(".png")).collect(Collectors.toList())), "");

		ComboBoxUtil.updateComboBoxContents(leggingsModelTexture, ListUtils.merge(Collections.singleton("From armor"),
				mcreator.getFolderManager().getTexturesList(TextureType.ENTITY).stream().map(File::getName)
						.filter(s -> s.endsWith(".png")).collect(Collectors.toList())), "");

		ComboBoxUtil.updateComboBoxContents(bootsModelTexture, ListUtils.merge(Collections.singleton("From armor"),
				mcreator.getFolderManager().getTexturesList(TextureType.ENTITY).stream().map(File::getName)
						.filter(s -> s.endsWith(".png")).collect(Collectors.toList())), "");

		ComboBoxUtil.updateComboBoxContents(helmetItemRenderType, ListUtils.merge(Arrays.asList(normal, tool),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(bodyItemRenderType, ListUtils.merge(Arrays.asList(normal, tool),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(leggingsItemRenderType, ListUtils.merge(Arrays.asList(normal, tool),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));

		ComboBoxUtil.updateComboBoxContents(bootsItemRenderType, ListUtils.merge(Arrays.asList(normal, tool),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));

		List<File> armors = mcreator.getFolderManager().getTexturesList(TextureType.ARMOR);
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
			ImageIcon bg1 = new ImageIcon(
					ImageUtils.resize(new ImageIcon(armorTextures[0].getAbsolutePath()).getImage(),
							64 * ARMOR_TEXTURE_SIZE_FACTOR, 32 * ARMOR_TEXTURE_SIZE_FACTOR));
			ImageIcon bg2 = new ImageIcon(
					ImageUtils.resize(new ImageIcon(armorTextures[1].getAbsolutePath()).getImage(),
							64 * ARMOR_TEXTURE_SIZE_FACTOR, 32 * ARMOR_TEXTURE_SIZE_FACTOR));
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
		helmetSpecialInformation.setSelectedProcedure(armor.helmetSpecialInformation);
		bodySpecialInformation.setSelectedProcedure(armor.bodySpecialInformation);
		leggingsSpecialInformation.setSelectedProcedure(armor.leggingsSpecialInformation);
		bootsSpecialInformation.setSelectedProcedure(armor.bootsSpecialInformation);
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

		helmetCollapsiblePanel.toggleVisibility(
				helmetModel.getSelectedItem() != defaultModel || armor.helmetSpecialInformation.getName() != null
						|| !armor.helmetSpecialInformation.getFixedValue().isEmpty());
		bodyCollapsiblePanel.toggleVisibility(
				bodyModel.getSelectedItem() != defaultModel || armor.bodySpecialInformation.getName() != null
						|| !armor.bodySpecialInformation.getFixedValue().isEmpty());
		leggingsCollapsiblePanel.toggleVisibility(
				leggingsModel.getSelectedItem() != defaultModel || armor.leggingsSpecialInformation.getName() != null
						|| !armor.leggingsSpecialInformation.getFixedValue().isEmpty());
		bootsCollapsiblePanel.toggleVisibility(
				bootsModel.getSelectedItem() != defaultModel || armor.bootsSpecialInformation.getName() != null
						|| !armor.bootsSpecialInformation.getFixedValue().isEmpty());

		helmetImmuneToFire.setSelected(armor.helmetImmuneToFire);
		bodyImmuneToFire.setSelected(armor.bodyImmuneToFire);
		leggingsImmuneToFire.setSelected(armor.leggingsImmuneToFire);
		bootsImmuneToFire.setSelected(armor.bootsImmuneToFire);

		helmetPiglinNeutral.setSelectedProcedure(armor.helmetPiglinNeutral);
		bodyPiglinNeutral.setSelectedProcedure(armor.bodyPiglinNeutral);
		leggingsPiglinNeutral.setSelectedProcedure(armor.leggingsPiglinNeutral);
		bootsPiglinNeutral.setSelectedProcedure(armor.bootsPiglinNeutral);

		Model helmetItemModel = armor.getHelmetItemModel();
		if (helmetItemModel != null)
			helmetItemRenderType.setSelectedItem(helmetItemModel);
		Model bodyItemModel = armor.getBodyItemModel();
		if (bodyItemModel != null)
			bodyItemRenderType.setSelectedItem(bodyItemModel);
		Model leggingsItemModel = armor.getLeggingsItemModel();
		if (leggingsItemModel != null)
			leggingsItemRenderType.setSelectedItem(leggingsItemModel);
		Model bootsItemModel = armor.getBootsItemModel();
		if (bootsItemModel != null)
			bootsItemRenderType.setSelectedItem(bootsItemModel);

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
		armor.helmetSpecialInformation = helmetSpecialInformation.getSelectedProcedure();
		armor.bodySpecialInformation = bodySpecialInformation.getSelectedProcedure();
		armor.leggingsSpecialInformation = leggingsSpecialInformation.getSelectedProcedure();
		armor.bootsSpecialInformation = bootsSpecialInformation.getSelectedProcedure();
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
		armor.helmetImmuneToFire = helmetImmuneToFire.isSelected();
		armor.bodyImmuneToFire = bodyImmuneToFire.isSelected();
		armor.leggingsImmuneToFire = leggingsImmuneToFire.isSelected();
		armor.bootsImmuneToFire = bootsImmuneToFire.isSelected();
		armor.helmetPiglinNeutral = helmetPiglinNeutral.getSelectedProcedure();
		armor.bodyPiglinNeutral = bodyPiglinNeutral.getSelectedProcedure();
		armor.leggingsPiglinNeutral = leggingsPiglinNeutral.getSelectedProcedure();
		armor.bootsPiglinNeutral = bootsPiglinNeutral.getSelectedProcedure();

		Model.Type helmetModelType = Objects.requireNonNull(helmetItemRenderType.getSelectedItem()).getType();
		armor.helmetItemRenderType = 0;
		if (helmetModelType == Model.Type.JSON)
			armor.helmetItemRenderType = 1;
		else if (helmetModelType == Model.Type.OBJ)
			armor.helmetItemRenderType = 2;
		armor.helmetItemCustomModelName = Objects.requireNonNull(helmetItemRenderType.getSelectedItem())
				.getReadableName();

		Model.Type bodyModelType = Objects.requireNonNull(bodyItemRenderType.getSelectedItem()).getType();
		armor.bodyItemRenderType = 0;
		if (bodyModelType == Model.Type.JSON)
			armor.bodyItemRenderType = 1;
		else if (bodyModelType == Model.Type.OBJ)
			armor.bodyItemRenderType = 2;
		armor.bodyItemCustomModelName = Objects.requireNonNull(bodyItemRenderType.getSelectedItem()).getReadableName();

		Model.Type leggingsModelType = Objects.requireNonNull(leggingsItemRenderType.getSelectedItem()).getType();
		armor.leggingsItemRenderType = 0;
		if (leggingsModelType == Model.Type.JSON)
			armor.leggingsItemRenderType = 1;
		else if (leggingsModelType == Model.Type.OBJ)
			armor.leggingsItemRenderType = 2;
		armor.leggingsItemCustomModelName = Objects.requireNonNull(leggingsItemRenderType.getSelectedItem())
				.getReadableName();

		Model.Type bootsModelType = Objects.requireNonNull(bootsItemRenderType.getSelectedItem()).getType();
		armor.bootsItemRenderType = 0;
		if (bootsModelType == Model.Type.JSON)
			armor.bootsItemRenderType = 1;
		else if (bootsModelType == Model.Type.OBJ)
			armor.bootsItemRenderType = 2;
		armor.bootsItemCustomModelName = Objects.requireNonNull(bootsItemRenderType.getSelectedItem())
				.getReadableName();

		return armor;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-armor");
	}

}
