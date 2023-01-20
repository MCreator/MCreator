/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.element.parts.gui;

import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.modgui.CustomGUIGUI;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.Workspace;

import java.awt.*;

public class EntityModel extends GUIComponent {

	public Procedure entityModel;
	public Procedure displayCondition;
	public double scale;
	public final int id = this.getID(this);

	public EntityModel(int x, int y, Procedure entityModel, Procedure displayCondition, double scale) {
		super(x, y);
		this.entityModel = entityModel;
		this.displayCondition = displayCondition;
		this.scale = scale;
	}

	@Override public String getName() {
		return "entity_model_" + this.id;
	}

	@Override public void paintComponent(int cx, int cy, WYSIWYGEditor wysiwygEditor, Graphics2D g) {
		g.drawImage(MinecraftImageGenerator.generateSpawnEggIcon(Color.blue, Color.pink).getImage(), cx, cy, 20, 20,
				wysiwygEditor);
	}

	@Override public int getWidth(Workspace workspace) {
		return 20;
	}

	@Override public int getHeight(Workspace workspace) {
		return 20;
	}

	@Override public int getWeight() {
		return -1;
	}

	public int getID(EntityModel entityModel) {
		int id = 0;
		for (GUIComponent component : CustomGUIGUI.staticComponentList.getComponentList()) {
			if (component instanceof EntityModel model) {
				if (model.equals(entityModel))
					break;
				id++;
			}
		}
		return id;
	}
}
