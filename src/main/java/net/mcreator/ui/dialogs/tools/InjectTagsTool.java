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

package net.mcreator.ui.dialogs.tools;

import com.google.common.base.CaseFormat;
import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Tag;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class InjectTagsTool {

	private static void open(MCreator mcreator) {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.tools.inject_tags.title"), true);
		dialog.setLayout(new BorderLayout(10, 10));
		dialog.setIconImage(UIRES.get("16px.injecttags").getImage());

		dialog.add("North", PanelUtils.join(FlowLayout.LEFT, L10N.label("dialog.tools.inject_tags.text_top")));

		JPanel props = new JPanel(new GridLayout(0, 1, 2, 2));

		JScrollPane scrollPane = new JScrollPane(props);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);

		dialog.add("Center", scrollPane);

		JButton ok = L10N.button("dialog.tools.inject_tags.confirm");
		JButton canecel = L10N.button(UIManager.getString("OptionPane.cancelButtonText"));
		canecel.addActionListener(e -> dialog.setVisible(false));
		dialog.add("South", PanelUtils.join(ok, canecel));

		List<Consumer<Boolean>> callables = new ArrayList<>();

		callables.add(addTag(mcreator, props, "dirt", "forge", "Blocks", true));
		callables.add(addTag(mcreator, props, "logs", "minecraft", "Blocks", true));
		callables.add(addTag(mcreator, props, "fences", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "wooden_fences", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "walls", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "small_flowers", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "tall_flowers", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "valid_spawn", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "impermeable", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "beacon_base_blocks", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "leaves", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "climbable", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "fire", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "dragon_immune", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "wither_immune", "minecraft", "Blocks", false));
		callables.add(addTag(mcreator, props, "arrows", "minecraft", "Items", false));
		callables.add(addTag(mcreator, props, "arrows", "minecraft", "Entities", false));
		callables.add(addTag(mcreator, props, "impact_projectiles", "minecraft", "Entities", false));
		callables.add(addTag(mcreator, props, "planks", "minecraft", "Items", false));
		callables.add(addTag(mcreator, props, "flowers", "minecraft", "Items", false));
		callables.add(addTag(mcreator, props, "small_flowers", "minecraft", "Items", false));
		callables.add(addTag(mcreator, props, "beehive_inhabitors", "minecraft", "Entities", false));
		callables.add(addTag(mcreator, props, "raiders", "minecraft", "Entities", false));
		callables.add(addTag(mcreator, props, "skeletons", "minecraft", "Entities", false));
		callables.add(addTag(mcreator, props, "tick", "minecraft", "Functions", false));
		callables.add(addTag(mcreator, props, "load", "minecraft", "Functions", false));

		ok.addActionListener(e -> {
			dialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			callables.forEach(c -> c.accept(false));
			mcreator.mv.updateMods();
			dialog.setCursor(Cursor.getDefaultCursor());
			dialog.setVisible(false);
		});

		dialog.setSize(740, 420);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);
	}

	private static Consumer<Boolean> addTag(MCreator mcreator, JPanel panel, String name, String namespace, String type,
			boolean checked) {
		boolean existing = mcreator.getWorkspace().getModElementByName(getNameForTag(name, type)) != null;

		JCheckBox box = new JCheckBox(
				"<html><kbd>" + namespace + ":" + name + (existing ? (" -> " + getNameForTag(name, type)) : "")
						+ "</kbd><small><br>" + L10N
						.t("dialog.tools.inject_tags.tag." + type.toLowerCase(Locale.ENGLISH) + "." + namespace + "."
								+ name));
		box.setSelected(checked);

		JLabel icon = new JLabel();
		if (type.equals("Blocks"))
			icon.setIcon(new ImageIcon(
					ImageUtils.resizeAA(TiledImageCache.getModTypeIcon(ModElementType.BLOCK).getImage(), 32)));
		if (type.equals("Items"))
			icon.setIcon(new ImageIcon(
					ImageUtils.resizeAA(TiledImageCache.getModTypeIcon(ModElementType.ITEM).getImage(), 32)));
		if (type.equals("Functions"))
			icon.setIcon(new ImageIcon(
					ImageUtils.resizeAA(TiledImageCache.getModTypeIcon(ModElementType.FUNCTION).getImage(), 32)));
		if (type.equals("Entities"))
			icon.setIcon(new ImageIcon(
					ImageUtils.resizeAA(TiledImageCache.getModTypeIcon(ModElementType.MOB).getImage(), 32)));

		panel.add(PanelUtils.centerAndEastElement(box, icon));

		if (existing)
			box.setEnabled(false);

		return altcondition -> {
			if (box.isSelected() || altcondition)
				injectTagToWorkspace(mcreator, name, namespace, type);
		};
	}

	private static String getNameForTag(String name, String type) {
		if (name.endsWith("s"))
			name = name.substring(0, name.length() - 1);

		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name.replace("_blocks", "")) + type;
	}

	private static void injectTagToWorkspace(MCreator mcreator, String name, String namespace, String type) {
		String modElementName = getNameForTag(name, type);
		Workspace workspace = mcreator.getWorkspace();

		if (workspace.getModElementByName(modElementName) == null) {
			Tag tag = new Tag(new ModElement(workspace, modElementName, ModElementType.TAG));
			tag.name = name;
			tag.namespace = namespace;
			tag.type = type;

			tag.blocks = Collections.emptyList();
			tag.items = Collections.emptyList();
			tag.functions = Collections.emptyList();
			tag.entities = Collections.emptyList();

			workspace.getModElementManager().storeModElementPicture(tag);
			workspace.addModElement(tag.getModElement());
			workspace.getGenerator().generateElement(tag);
			workspace.getModElementManager().storeModElement(tag);
		}
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.tag"),
				e -> open(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				GeneratorConfiguration gc = actionRegistry.getMCreator().getGeneratorConfiguration();
				return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.TAG)
						!= GeneratorStats.CoverageStatus.NONE;
			}
		}.setIcon(UIRES.get("16px.injecttags"));
	}

}
