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

package net.mcreator.integration.javafx;

import javafx.embed.swing.JFXPanel;
import net.mcreator.ui.component.util.ThreadUtil;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

public class JavaFXThreadingRule implements TestRule {

	private static boolean jfxIsSetup;

	@Override public Statement apply(Statement statement, Description description) {
		return new OnJFXThreadStatement(statement);
	}

	private static class OnJFXThreadStatement extends Statement {

		private final Statement statement;

		OnJFXThreadStatement(Statement aStatement) {
			statement = aStatement;
		}

		private Throwable rethrownException = null;

		@Override public void evaluate() throws Throwable {
			if (!jfxIsSetup) {
				setupJavaFX();
				jfxIsSetup = true;
			}
			CountDownLatch countDownLatch = new CountDownLatch(1);
			ThreadUtil.runOnFxThread(() -> {
				try {
					statement.evaluate();
				} catch (Throwable e) {
					rethrownException = e;
				}
				countDownLatch.countDown();
			});
			countDownLatch.await();

			// if an exception was thrown by the statement during evaluation,
			// then re-throw it to fail the test
			if (rethrownException != null)
				throw rethrownException;
		}

		void setupJavaFX() throws InterruptedException {
			CountDownLatch latch = new CountDownLatch(1);
			SwingUtilities.invokeLater(() -> {
				new JFXPanel();
				latch.countDown();
			});
			latch.await();
		}

	}
}