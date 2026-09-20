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

package net.mcreator.ui.minecraft.boundingboxes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mcreator.element.types.interfaces.IBlockWithBoundingBox;
import net.mcreator.io.FileIO;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.TechnicalButton;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.resources.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class JBoundingBoxList extends JSimpleEntriesList<JBoundingBoxEntry, IBlockWithBoundingBox.BoxEntry> {

	private static final Logger LOG = LogManager.getLogger(JBoundingBoxList.class);

	@Nullable private final Supplier<Model> modelProvider;

	private final TechnicalButton genFromModel = L10N.technicalbutton("elementgui.common.gen_from_block_model");

	private final MCreator mcreator;

	public JBoundingBoxList(MCreator mcreator, IHelpContext gui, @Nullable Supplier<Model> modelProvider) {
		super(mcreator, gui);
		this.modelProvider = modelProvider;
		this.mcreator = mcreator;

		if (modelProvider != null) {
			genFromModel.addActionListener(_ -> generateBoundingBoxFromModel());
			topbar.add(genFromModel);
			modelChanged();
		}

		add.setText(L10N.t("elementgui.common.add_bounding_box"));

		entries.addPropertyChangeListener("boundingBoxChanged",
				_ -> firePropertyChange("boundingBoxChanged", false, true));

		ComponentUtils.makeSection(this, L10N.t("elementgui.common.bounding_box_entries"));
		setPreferredSize(new Dimension(getPreferredSize().width, (int) (mcreator.getSize().height * 0.6)));
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		genFromModel.setEnabled(enabled);
	}

	@Override public void entryAddedByUserHandler() {
		firePropertyChange("boundingBoxChanged", false, true);
	}

	@Override
	protected JBoundingBoxEntry newEntry(JPanel parent, List<JBoundingBoxEntry> entryList, boolean userAction) {
		return new JBoundingBoxEntry(parent, entryList, mcreator.getGeneratorConfiguration().getGeneratorFlavor());
	}

	public void modelChanged() {
		if (modelProvider != null)
			genFromModel.setVisible(modelProvider.get() != null && supportsGenerateFromModel(modelProvider.get()));
	}

	private static boolean supportsGenerateFromModel(Model model) {
		return model.getType() == Model.Type.JSON || model.getType() == Model.Type.BEDROCK;
	}

	private void generateBoundingBoxFromModel() {
		if (modelProvider != null) {
			Model model = modelProvider.get();
			if (model != null && supportsGenerateFromModel(model)) {
				try {
					JsonObject modelJSON = JsonParser.parseString(FileIO.readFileToString(model.getFile()))
							.getAsJsonObject();
					List<IBlockWithBoundingBox.BoxEntry> boxEntries = model.getType() == Model.Type.BEDROCK ?
							boxesFromBedrockModel(modelJSON) :
							boxesFromJSONModel(modelJSON);
					if (boxEntries != null)
						setEntries(boxEntries);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(mcreator,
							L10N.t("elementgui.common.gen_from_block_model_failed.message"),
							L10N.t("elementgui.common.gen_from_block_model_failed.title"), JOptionPane.ERROR_MESSAGE);
					LOG.error("Failed to process corrupt block model!", e);
				}
			}
		}
	}

	@Nullable private static List<IBlockWithBoundingBox.BoxEntry> boxesFromJSONModel(JsonObject modelJSON) {
		if (!modelJSON.has("elements"))
			return null;

		List<IBlockWithBoundingBox.BoxEntry> boxEntries = new ArrayList<>();
		for (JsonElement element : modelJSON.get("elements").getAsJsonArray()) {
			JsonArray from = element.getAsJsonObject().get("from").getAsJsonArray();
			JsonArray to = element.getAsJsonObject().get("to").getAsJsonArray();

			IBlockWithBoundingBox.BoxEntry box = new IBlockWithBoundingBox.BoxEntry();
			box.mx = from.get(0).getAsDouble();
			box.my = from.get(1).getAsDouble();
			box.mz = from.get(2).getAsDouble();
			box.Mx = to.get(0).getAsDouble();
			box.My = to.get(1).getAsDouble();
			box.Mz = to.get(2).getAsDouble();

			boxEntries.add(box);
		}
		return boxEntries;
	}

	/**
	 * Bedrock geometry cubes are placed relative to the bottom center of the block with the X axis pointing west,
	 * while bounding boxes are relative to the bottom north-west corner with the X axis pointing east.
	 * Cube and bone rotations are ignored, the same way element rotations of Java models are ignored.
	 * Only the first geometry of the file is used, which is what the block references.
	 */
	@Nullable private static List<IBlockWithBoundingBox.BoxEntry> boxesFromBedrockModel(JsonObject modelJSON) {
		if (!modelJSON.has("minecraft:geometry") || modelJSON.getAsJsonArray("minecraft:geometry").isEmpty())
			return null;

		JsonObject geometry = modelJSON.getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject();
		if (!geometry.has("bones"))
			return null;

		List<IBlockWithBoundingBox.BoxEntry> boxEntries = new ArrayList<>();
		for (JsonElement bone : geometry.getAsJsonArray("bones")) {
			if (!bone.getAsJsonObject().has("cubes"))
				continue;

			for (JsonElement cubeElement : bone.getAsJsonObject().getAsJsonArray("cubes")) {
				JsonObject cube = cubeElement.getAsJsonObject();
				JsonArray origin = cube.getAsJsonArray("origin");
				JsonArray size = cube.getAsJsonArray("size");
				double inflate = cube.has("inflate") ? cube.get("inflate").getAsDouble() : 0;

				IBlockWithBoundingBox.BoxEntry box = new IBlockWithBoundingBox.BoxEntry();
				box.mx = 8 - (origin.get(0).getAsDouble() + size.get(0).getAsDouble()) - inflate;
				box.Mx = 8 - origin.get(0).getAsDouble() + inflate;
				box.my = origin.get(1).getAsDouble() - inflate;
				box.My = origin.get(1).getAsDouble() + size.get(1).getAsDouble() + inflate;
				box.mz = origin.get(2).getAsDouble() + 8 - inflate;
				box.Mz = origin.get(2).getAsDouble() + size.get(2).getAsDouble() + 8 + inflate;

				boxEntries.add(box);
			}
		}
		return boxEntries;
	}

	public boolean isFullCube() {
		return entryList.stream().anyMatch(JBoundingBoxEntry::isNotEmpty) && entryList.stream()
				.filter(JBoundingBoxEntry::isNotEmpty).allMatch(JBoundingBoxEntry::isFullCube);
	}

}
