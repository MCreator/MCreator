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

package net.mcreator.ui.modgui;

import net.mcreator.element.types.Tree;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class TreeGUI extends ModElementGUI<Tree> {

	private TextureHolder texture;

	private final JSpinner light = new JSpinner(new SpinnerNumberModel(9, 0, 15, 1));
	private final JSpinner mx = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.1));
	private final JSpinner my = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.1));
	private final JSpinner mz = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.1));
	private final JSpinner Mx = new JSpinner(new SpinnerNumberModel(1, -100, 100, 0.1));
	private final JSpinner My = new JSpinner(new SpinnerNumberModel(1, -100, 100, 0.1));
	private final JSpinner Mz = new JSpinner(new SpinnerNumberModel(1, -100, 100, 0.1));
	private final JSpinner minHeight = new JSpinner(new SpinnerNumberModel(7, 0, 256, 1));
	private final JSpinner randomHeight = new JSpinner(new SpinnerNumberModel(0, 0, 256, 1));
	private final JSpinner foliageHeight = new JSpinner(new SpinnerNumberModel(5, 0, 1000, 1));
	private final JSpinner foliageRadius = new JSpinner(new SpinnerNumberModel(2, 0, 100, 1));
	private final JSpinner foliageRadiusRandom = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
	private final JSpinner maxWaterDepth = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));

	private MCItemHolder treeStem;
	private MCItemHolder treeBranch;

	private final ValidationGroup page1group = new ValidationGroup();

	public TreeGUI(MCreator mcreator, @NotNull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {

		treeStem = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		treeBranch = new MCItemHolder(mcreator, ElementUtil::loadBlocks);

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));

		pane2.setOpaque(false);

		JPanel sbbp1 = new JPanel(new GridLayout(3, 2, 5, 5));

		sbbp1.add(HelpUtils.wrapWithHelpButton(this.withEntry("tree/stem_block"), new JLabel("Block for log:")));
		sbbp1.add(PanelUtils.join(treeStem));

		sbbp1.add(HelpUtils.wrapWithHelpButton(this.withEntry("tree/branch_block"), new JLabel("Block for leaves:")));
		sbbp1.add(PanelUtils.join(treeBranch));

		sbbp1.setOpaque(false);

		JPanel sbbp2 = new JPanel(new GridLayout(4, 2, 5, 5));

		sbbp2.add(new JLabel("Minimal height, random height: "));
		sbbp2.add(PanelUtils
				.join(HelpUtils.wrapWithHelpButton(this.withEntry("tree/min_height"), minHeight),
						HelpUtils.wrapWithHelpButton(this.withEntry("tree/random_height"), randomHeight)));

		sbbp2.add(new JLabel("Foliage radius, foliage random radium: "));
		sbbp2.add(PanelUtils
				.join(HelpUtils.wrapWithHelpButton(this.withEntry("tree/foliage_radius"), foliageRadius),
						HelpUtils.wrapWithHelpButton(this.withEntry("tree/foliage_random_radius"), foliageRadiusRandom)));

		sbbp2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("tree/foliage_height"), new JLabel("Foliage height:")));
		sbbp2.add(foliageHeight);

		sbbp2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("tree/max_water_depth"), new JLabel("Maximal water depth:")));
		sbbp2.add(maxWaterDepth);

		sbbp2.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				"Tree properties", 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		sbbp2.setOpaque(false);


		JPanel panels = new JPanel(new BorderLayout(15, 25));

		panels.add("Center", PanelUtils.join(sbbp1));
		panels.add("South", sbbp2);

		panels.setOpaque(false);

		JPanel cont = new JPanel(new BorderLayout(30, 30));
		cont.setOpaque(false);

		cont.add("Center", PanelUtils.join(panels));

		pane2.add("Center", PanelUtils.totalCenterInPanel(cont));

		page1group.addValidationElement(treeStem);
		page1group.addValidationElement(treeBranch);

		treeStem.setValidator(new MCItemHolderValidator(treeStem));
		treeBranch.setValidator(new MCItemHolderValidator(treeBranch));

		addPage("Tree", pane2);

	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override protected void openInEditingMode(Tree tree) {
		treeStem.setBlock(tree.treeStem);
		treeBranch.setBlock(tree.treeBranch);
		minHeight.setValue(tree.minHeight);
		randomHeight.setValue(tree.randomHeight);
		foliageHeight.setValue(tree.foliageHeight);
		foliageRadius.setValue(tree.foliageRadius);
		foliageRadiusRandom.setValue(tree.foliageRadiusRandom);
		maxWaterDepth.setValue(tree.maxWaterDepth);
	}

	@Override public Tree getElementFromGUI() {
		Tree tree = new Tree(modElement);
		tree.treeStem = treeStem.getBlock();
		tree.treeBranch = treeBranch.getBlock();
		tree.minHeight = (int) minHeight.getValue();
		tree.randomHeight = (int) randomHeight.getValue();
		tree.foliageHeight = (int) foliageHeight.getValue();
		tree.foliageRadius = (int) foliageRadius.getValue();
		tree.foliageRadiusRandom = (int) foliageRadiusRandom.getValue();
		return tree;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-tree");
	}
}
