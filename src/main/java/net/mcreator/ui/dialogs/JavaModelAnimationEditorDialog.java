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

package net.mcreator.ui.dialogs;

import net.mcreator.minecraft.JavaModels;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.EntityAnimationsLoader;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.StringUtils;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaModelAnimationEditorDialog {

	public static String openAnimationEditorDialog(MCreator mcreator, String modelSource) {
		JavaClassSource classJavaSource = (JavaClassSource) Roaster.parse(modelSource);

		List<String> vc = JavaModels.getModelParts(classJavaSource);

		JPanel options = new JPanel(new GridLayout(vc.size(), 2, 10, 10));

		Map<String, JComboBox<String>> animations = new HashMap<>();

		for (String part : vc) {
			List<String> types = EntityAnimationsLoader.getAnimationIDs();
			types.sort(String::compareTo);
			JComboBox<String> box = new JComboBox<>(types.toArray(new String[0]));
			box.setSelectedItem("No animation");
			animations.put(part, box);
			options.add(new JLabel(StringUtils.abbreviateString(part, 20) + " animation: "));
			options.add(box);
		}

		JScrollPane sp = new JScrollPane(options);
		sp.getVerticalScrollBar().setUnitIncrement(15);

		if (animations.size() > 10)
			sp.setPreferredSize(new Dimension(450, 400));

		int opt = JOptionPane.showOptionDialog(mcreator, sp, L10N.t("dialog.animation_editor.title_wizard"),
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				new String[] { L10N.t("dialog.animation_editor.action_set_new"),
						L10N.t("dialog.animation_editor.action_keep_current") }, null);

		if (opt == 0) {
			int model_version = JavaModels.getModelVersionAndPrepareCodeForAnimations(classJavaSource);
			if (model_version == 1) {
				classJavaSource.addMethod(JavaModels.getAnimationsModelType1(animations));
			} else {
				classJavaSource.addMethod(JavaModels.getAnimationsModelType0(animations));
			}
		}
		// Below: legacy model fixers for Techne models (only usable for legacy models (below 1.17))
		else if (mcreator.getGeneratorConfiguration().getJavaModelsKey().equals("legacy") && classJavaSource.toString()
				.contains("setRotationAngles(f, f1, f2, f3, f4, f5);")) { // outdated model format
			List<MethodSource<JavaClassSource>> methods = classJavaSource.getMethods();
			for (MethodSource<JavaClassSource> method : methods) {
				if (method.getName().equals("setRotationAngles"))
					classJavaSource.removeMethod(method);
			}
		}
		// Handling for keeping existing animations for non-legacy models
		else {
			return null; // with null, we indicate no change in model code
		}

		return classJavaSource.toString();
	}

}
