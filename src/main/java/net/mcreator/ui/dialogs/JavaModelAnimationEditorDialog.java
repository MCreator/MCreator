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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.BuiltInEntityAnimations;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.StringUtils;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class JavaModelAnimationEditorDialog {

	public static String openAnimationEditorDialog(MCreator mcreator, String modelSource) {
		JavaClassSource classJavaSource = (JavaClassSource) Roaster.parse(modelSource);

		Vector<String> vc = getModelParts(classJavaSource);

		JPanel options = new JPanel(new GridLayout(vc.size(), 2, 10, 10));

		Map<String, JComboBox<String>> animations = new HashMap<>();

		for (String part : vc) {
			List<String> types = new ArrayList<>(BuiltInEntityAnimations.getAllIDs());
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
						L10N.t("dialog.animation_editor.action_keep_current") },
				L10N.t("dialog.animation_editor.action_set"));

		if (opt == 0) {
			List<MethodSource<JavaClassSource>> methods = classJavaSource.getMethods();
			for (MethodSource<JavaClassSource> method : methods) {
				if (method.getName().equals("setRotationAngles"))
					classJavaSource.removeMethod(method);
			}

			StringBuilder anim = new StringBuilder();

			for (Map.Entry<String, JComboBox<String>> animation : animations.entrySet()) {
				String selected = (String) animation.getValue().getSelectedItem();
				if (selected != null) {
					JsonArray animationCodes = BuiltInEntityAnimations.getAnimations(selected);
					for (JsonElement animationCode : animationCodes) {
						anim.append("this.").append(animation.getKey()).append(animationCode.getAsString()).append("\n");
					}
				}
			}

			classJavaSource.addMethod(
					"public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {super.setRotationAngles(f, f1, f2, f3, f4, f5, e);"
							+ anim.toString() + "}");
		} else if (classJavaSource.toString()
				.contains("setRotationAngles(f, f1, f2, f3, f4, f5);")) { // outdated model format
			List<MethodSource<JavaClassSource>> methods = classJavaSource.getMethods();
			for (MethodSource<JavaClassSource> method : methods) {
				if (method.getName().equals("setRotationAngles"))
					classJavaSource.removeMethod(method);
			}

			classJavaSource.addMethod(
					"public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {super.setRotationAngles(f, f1, f2, f3, f4, f5, e);}");
		} else if (!classJavaSource.toString().contains("setRotationAngles(")) {
			// if not setRotationAngles is defined in model, we add it now
			classJavaSource.addMethod(
					"public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {super.setRotationAngles(f, f1, f2, f3, f4, f5, e);}");
		} else {
			return null; // with null, we indicate no change in model code
		}

		return classJavaSource.toString();
	}

	public static Vector<String> getModelParts(JavaClassSource classJavaSource) {
		Vector<String> parts = new Vector<>();
		List<FieldSource<JavaClassSource>> fields = classJavaSource.getFields();
		for (FieldSource<JavaClassSource> field : fields)
			if (field.getType().getName().contains("ModelRenderer"))
				parts.add(field.getName());
		return parts;
	}
}
