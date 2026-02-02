/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.io.writer;

import net.mcreator.io.TrackingFileIO;
import net.mcreator.util.TestUtil;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

import javax.annotation.Nullable;
import java.io.File;

public class JSWriter {

	private static final Logger LOG = LogManager.getLogger("JS Writer");

	public static void writeJSToFile(@Nullable Workspace workspace, String srcjson, File file) {
		TrackingFileIO.writeFile(workspace, formatJS(srcjson), file);
	}

	public static String formatJS(String srcjs) {
		String jsout;
		try {
			CompilerEnvirons env = new CompilerEnvirons();
			env.setRecoverFromErrors(true);
			env.setRecordingComments(true);
			env.setRecordingLocalJsDocComments(true);
			env.setLanguageVersion(Context.VERSION_ES6);

			Parser parser = new Parser(env, env.getErrorReporter());
			AstRoot astRoot = parser.parse(srcjs, null, 1);

			return astRoot.toSource(0);
		} catch (Exception e) {
			LOG.error("JS Prettify failed, error: {}", e.getMessage(), e);
			TestUtil.failIfTestingEnvironment();
			jsout = srcjs;
		}
		return jsout;
	}

}
