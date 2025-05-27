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
import net.mcreator.generator.GeneratorWrapper;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.MCreator;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LootTablePreview extends JLayeredPane {

	private static final int CONTAINER_WIDTH = 400;
	private static final int CONTAINER_HEIGHT = 150;

	private final MCreator mcreator;
	private final JPanel slotsPanel = new JPanel(new GridLayout(3, 9));

	public LootTablePreview(MCreator mcreator) {
		this.mcreator = mcreator;

		// Draw container and slots
		BufferedImage container = MinecraftImageGenerator.generateBackground(CONTAINER_WIDTH, CONTAINER_HEIGHT);
		Image slot = ImageUtils.resize(MinecraftImageGenerator.generateItemSlot(), 41, 40);

		Graphics2D g = (Graphics2D) container.getGraphics();
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 3; j++)
				g.drawImage(slot, i * 41 + 14, j * 40 + 14, null);

		g.dispose();

		JLabel previewContainer = new JLabel(new ImageIcon(container));
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
		List<LootTable.Pool.Entry> allEntries = new ArrayList<>();

		// Loop through all pools to determine items to generate
		for (LootTable.Pool pool : lootEntries) {
			int rolls = rand.nextInt(pool.minrolls, pool.maxrolls + 1);
			if (pool.hasbonusrolls) {
				rolls += rand.nextInt(pool.minbonusrolls, pool.maxbonusrolls + 1);
			}

			List<LootTable.Pool.Entry> selectedEntries = weightedChoice(rand, pool.entries, rolls);
			allEntries.addAll(selectedEntries);
		}

		// Process counts and split stacks
		List<LootTable.Pool.Entry> processedEntries = new ArrayList<>();
		for (LootTable.Pool.Entry entry : allEntries) {
			if (entry.minCount > entry.maxCount)
				continue;

			int count = rand.nextInt(entry.minCount, entry.maxCount + 1);

			if (count == 0)
				continue;

			for (int stack : splitItems(rand, count)) {
				LootTable.Pool.Entry stackEntry = new LootTable.Pool.Entry();
				stackEntry.item = entry.item;
				stackEntry.minCount = stackEntry.maxCount = stack;

				processedEntries.add(stackEntry);
			}
		}

		List<Integer> slots = getRandomSlots(rand, processedEntries.size());

		// Display items
		for (int i = 0; i < Math.min(27, processedEntries.size()); i++) {
			LootTable.Pool.Entry entry = processedEntries.get(i);
			JLabel slot = (JLabel) slotsPanel.getComponent(slots.get(i));

			String unmappedName = entry.item.getUnmappedValue();
			MCItem item;
			String id;

			if (unmappedName.startsWith(NameMapper.MCREATOR_PREFIX)) {
				String plainName = GeneratorWrapper.getElementPlainName(unmappedName);
				ModElement modElement = mcreator.getWorkspace().getModElementByName(plainName);

				item = modElement.getMCItems().stream().filter(e -> e.getType().equals("item")).findFirst()
						.orElse(null);
				id = mcreator.getWorkspaceSettings().getModID() + ":" + modElement.getRegistryName();
			} else {
				item = new MCItem(entry.item.getDataListEntry().get());
				id = "minecraft:" + entry.item.getMappedValue(1);
			}

			if (item == null || id.equals("minecraft:air"))
				continue;

			slot.setIcon(MinecraftImageGenerator.generateItemWithCount(item, entry.minCount));
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

	private List<Integer> getRandomSlots(Random rand, int count) {
		List<Integer> slots = IntStream.range(0, 27).boxed().collect(Collectors.toList());
		Collections.shuffle(slots, rand);
		return slots.subList(0, Math.min(27, count));
	}

	private List<Integer> splitItems(Random rand, int totalCount) {
		List<Integer> stacks = new ArrayList<>();

		while (totalCount > 0) {
			int maxPossible = Math.min(64, totalCount);
			int stack = 1 +  rand.nextInt(maxPossible);
			stacks.add(stack);
			totalCount -= stack;
		}

		return stacks;
	}

	private List<LootTable.Pool.Entry> weightedChoice(Random rand, List<LootTable.Pool.Entry> entries, int count) {
		List<LootTable.Pool.Entry> result = new ArrayList<>();
		int totalWeight = entries.stream().mapToInt(e -> e.weight).sum();

		for (int i = 0; i < count; i++) {
			int idx = 0;
			double random = rand.nextDouble() * totalWeight;

			for (LootTable.Pool.Entry entry : entries) {
				idx += entry.weight;
				if (random < idx) {
					result.add(entry);
					break;
				}
			}
		}

		return result;
	}
}
