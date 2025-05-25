/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.loottable;

import net.mcreator.element.types.LootTable;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.UIRES;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LootTablePreview extends JLayeredPane {

	private static final int CONTAINER_WIDTH = 400;
	private static final int CONTAINER_HEIGHT = 150;

	private final MCreator mcreator;
	private final JPanel slotsPanel = new JPanel(new GridLayout(3, 9));

	public LootTablePreview(MCreator mcreator) {
		this.mcreator = mcreator;

		ImageIcon previewContainerImage = new ImageIcon(UIRES.get("container").getImage()
				.getScaledInstance(CONTAINER_WIDTH, CONTAINER_HEIGHT, Image.SCALE_SMOOTH));

		JLabel previewContainer = new JLabel(previewContainerImage);
		previewContainer.setBounds(0, 0, CONTAINER_WIDTH, CONTAINER_HEIGHT);

		slotsPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
		slotsPanel.setBounds(0, 0, CONTAINER_WIDTH, CONTAINER_HEIGHT);
		slotsPanel.setOpaque(false);

		for (int i = 0; i < 27; i++) {
			slotsPanel.add(new JLabel() {{
				setHorizontalAlignment(SwingConstants.CENTER);
			}});
		}

		setPreferredSize(new Dimension(CONTAINER_WIDTH, CONTAINER_HEIGHT));
		setOpaque(false);

		add(previewContainer, JLayeredPane.DEFAULT_LAYER);
		add(slotsPanel, JLayeredPane.PALETTE_LAYER);
	}

	public void generateLootTable(List<LootTable.Pool> lootEntries) {
		Random rand = new Random(1L);

		clearLootTable();
		List<LootTable.Pool.Entry> entries = new ArrayList<>();
		List<Integer> slots = new ArrayList<>();

		// Loop through all pools to determine items to generate
		for (LootTable.Pool pool : lootEntries) {
			int rolls = rand.nextInt(pool.minrolls, pool.maxrolls + 1);
			if (pool.hasbonusrolls) {
				rolls += rand.nextInt(pool.minbonusrolls, pool.maxbonusrolls + 1);
			}

			List<LootTable.Pool.Entry> selectedEntries = weightedChoice(pool.entries, rolls);

			List<Integer> s = getRandomSlots(rand, rolls, slots);

			entries.addAll(selectedEntries);
			slots.addAll(s);
		}

		// Display items in slots
		for (int i = 0; i < entries.size(); i++) {
			LootTable.Pool.Entry entry = entries.get(i);
			JLabel slot = (JLabel) slotsPanel.getComponent(slots.get(i));

			if (entry.minCount > entry.maxCount)
				continue;

			int count = rand.nextInt(entry.minCount, entry.maxCount + 1);

			String unmappedName = entry.item.getUnmappedValue();
			MCItem item;
			String id;

			if (unmappedName.startsWith(NameMapper.MCREATOR_PREFIX)) {
				String plainName = StringUtils.substringBeforeLast(unmappedName.replace(NameMapper.MCREATOR_PREFIX, ""),
						".");
				ModElement modElement = mcreator.getWorkspace().getModElementByName(plainName);

				item = modElement.getMCItems().stream().filter(e -> e.getType().equals("item")).findFirst()
						.orElse(null);
				id = mcreator.getWorkspaceSettings().getModID() + ":" + modElement.getRegistryName();
			} else {
				item = new MCItem(entry.item.getDataListEntry().get());
				id = "minecraft:" + entry.item.getMappedValue(1);
			}

			if (item == null || id.equals("minecraft:air") || count == 0)
				continue;

			slot.setIcon(MinecraftImageGenerator.generateItemWithCount(item, count));
			slot.setToolTipText("<html>" + item.getReadableName() + "<br><small><font color='gray'>" + id);
		}
	}

	private void clearLootTable() {
		for (Component component : slotsPanel.getComponents()) {
			JLabel slot = (JLabel) component;
			slot.setIcon(null);
			slot.setToolTipText(null);
		}
	}

	private List<Integer> getRandomSlots(Random rand, int count, List<Integer> currentSlots) {
		Set<Integer> slots = new HashSet<>();

		while (slots.size() < count) {
			int s = rand.nextInt(0, 27);
			if (!currentSlots.contains(s))
				slots.add(s);
		}

		return new ArrayList<>(slots);
	}

	private List<LootTable.Pool.Entry> weightedChoice(List<LootTable.Pool.Entry> entries, int count) {
		List<LootTable.Pool.Entry> result = new ArrayList<>();
		int totalWeight = entries.stream().mapToInt(e -> e.weight).sum();

		for (int i = 0; i < count; i++) {
			int idx = 0;
			double random = Math.random() * totalWeight;

			for (; idx < entries.size(); idx++) {
				random -= entries.get(idx).weight;
				if (random <= 0)
					break;
			}

			if (idx >= entries.size())
				idx = entries.size() - 1;

			result.add(entries.get(idx));
		}

		return result;
	}
}
