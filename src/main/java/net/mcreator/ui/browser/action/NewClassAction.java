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

package net.mcreator.ui.browser.action;

import net.mcreator.io.writer.ClassWriter;
import net.mcreator.java.JavaConventions;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.JavaMemeberNameValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;

public class NewClassAction extends BasicAction {

	private static final Logger LOG = LogManager.getLogger(NewClassAction.class);

	public NewClassAction(ActionRegistry actionRegistry) {
		super(actionRegistry, "Java class", actionEvent -> {
			String classname = VOptionPane.showInputDialog(actionRegistry.getMCreator(),
					"<html><b>Enter the class name:</b>"
							+ "<br>Make sure the class name is a valid Java name without .java extension.<br>"
							+ "File will be added to the selected package or to default if none is selected.",
					"Class name", null, new OptionPaneValidatior() {
						@Override public Validator.ValidationResult validate(JComponent component) {
							return new JavaMemeberNameValidator((VTextField) component, true).validate();
						}
					});

			if (classname != null) {
				classname = JavaConventions.convertToValidClassName(classname);

				if (actionRegistry.getMCreator().getProjectBrowser().tree.getLastSelectedPathComponent() != null) {
					Object selection = ((DefaultMutableTreeNode) actionRegistry.getMCreator().getProjectBrowser().tree
							.getLastSelectedPathComponent()).getUserObject();
					if (selection instanceof File) {
						File filesel = ((File) selection);
						if (filesel.isFile())
							filesel = filesel.getParentFile();

						if (filesel.isDirectory()) {
							String path = filesel.getPath() + "/" + classname + ".java";

							String packagenm = "";

							try {
								String root = actionRegistry.getMCreator().getGenerator().getSourceRoot()
										.getCanonicalPath();
								String pathCan = new File(path).getCanonicalPath();
								String packagetm = pathCan.replace(root, "").replaceFirst("\\\\", "")
										.replaceFirst("/", "").replace(classname + ".java", "").replace("/", ".")
										.replace("\\", ".").trim();
								if (packagetm.endsWith("."))
									packagetm = packagetm.substring(0, packagetm.length() - 1);
								packagenm = packagetm;
							} catch (IOException e) {
								LOG.error(e.getMessage(), e);
							}

							String code = "";

							if (!packagenm.equals(""))
								code += "package " + packagenm + ";\n\n";

							code += "public class " + classname + " {\n\n\n}";

							ClassWriter.writeClassToFileWithoutQueue(actionRegistry.getMCreator().getWorkspace(), code,
									new File(path), true);

							actionRegistry.getMCreator().getProjectBrowser().reloadTree();
						}
					}
				}
			}
		});
		setIcon(UIRES.get("16px.class.gif"));
	}

}
