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

package net.mcreator.ui.chromium;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefPaintEvent;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

final class CefOsrBlackFlashFix {

	private static final Logger LOG = LogManager.getLogger(CefOsrBlackFlashFix.class);

	public static void apply(JComponent parent, CefBrowser browser, Component cefComponent) {
		cefComponent.setBackground(parent.getBackground());

		if (cefComponent instanceof GLCanvas glCanvas) {
			// -1 means it is time to render the actual browser
			AtomicInteger paintCount = new AtomicInteger(0);

			try {
				Consumer<CefPaintEvent> paintListener = cefPaintEvent -> {
					// For some reason, hiding the browser for the first two paints (show it on 3rd paint)
					// foxes the black flash we can observe in some cases
					if (paintCount.get() != -1 && paintCount.incrementAndGet() == 3) {
						paintCount.set(-1);
						glCanvas.display();
					}
				};

				Method addListener = browser.getClass().getMethod("addOnPaintListener", Consumer.class);
				addListener.setAccessible(true);
				addListener.invoke(browser, paintListener);
			} catch (Exception e) {
				// Immediately render if we can't add listener to detect the first paint
				paintCount.set(-1);

				LOG.warn("Failed to add paint listener", e);
			}

			glCanvas.addGLEventListener(new GLEventListener() {
				private final Color bg = parent.getBackground();

				@Override public void init(GLAutoDrawable drawable) {
					GL2 gl = drawable.getGL().getGL2();
					gl.glClearColor(bg.getRed() / 255f, bg.getGreen() / 255f, bg.getBlue() / 255f, 1f);
				}

				@Override public void display(GLAutoDrawable drawable) {
					if (paintCount.get() != -1) { // until we can render browser, render background color
						GL2 gl = drawable.getGL().getGL2();
						gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
					} else {
						// We have done our job, remove ourselves
						glCanvas.removeGLEventListener(this);
					}
				}

				@Override public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
				}

				@Override public void dispose(GLAutoDrawable drawable) {
				}
			});
		}
	}

}
