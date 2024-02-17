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
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
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

	public JBoundingBoxList(MCreator mcreator, IHelpContext gui, @Nullable Supplier<Model> modelProvider) {
		super(mcreator, gui);
		this.modelProvider = modelProvider;

		if (modelProvider != null) {
			genFromModel.addActionListener(e -> generateBoundingBoxFromModel());
			topbar.add(genFromModel);
			modelChanged();
		}

		add.setText(L10N.t("elementgui.common.add_bounding_box"));

		entries.addPropertyChangeListener("boundingBoxChanged",
				e -> firePropertyChange("boundingBoxChanged", false, true));

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.common.bounding_box_entries"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));
		setPreferredSize(new Dimension(getPreferredSize().width, (int) (mcreator.getSize().height * 0.6)));
	}

	@Override public void entryAddedByUserHandler() {
		firePropertyChange("boundingBoxChanged", false, true);
	}

	@Override
	protected JBoundingBoxEntry newEntry(JPanel parent, List<JBoundingBoxEntry> entryList, boolean userAction) {
		return new JBoundingBoxEntry(parent, entryList);
	}

	public void modelChanged() {
		if (modelProvider != null)
			genFromModel.setVisible(modelProvider.get() != null && modelProvider.get().getType() == Model.Type.JSON);
	}

	private void generateBoundingBoxFromModel() {
		if (modelProvider != null) {
			Model model = modelProvider.get();
			if (model != null && model.getType() == Model.Type.JSON) {
				try {
					JsonObject modelJSON = JsonParser.parseString(FileIO.readFileToString(model.getFile()))
							.getAsJsonObject();
					if (modelJSON.has("elements")) {
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

						setEntries(boxEntries);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(mcreator,
							L10N.t("elementgui.common.gen_from_block_model_failed.message"),
							L10N.t("elementgui.common.gen_from_block_model_failed.title"), JOptionPane.ERROR_MESSAGE);
					LOG.error("Failed to process corrupt block model!", e);
				}
			}
		}
	}

	public boolean isFullCube() {
		return entryList.stream().anyMatch(JBoundingBoxEntry::isNotEmpty) && entryList.stream()
				.filter(JBoundingBoxEntry::isNotEmpty).allMatch(JBoundingBoxEntry::isFullCube);
	}

}
