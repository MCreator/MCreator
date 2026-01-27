// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
// Modifications by Pylo and opensource contributors

package net.mcreator.ui.chromium.osr;

import com.jetbrains.cef.JCefAppConfig;
import net.mcreator.ui.laf.themes.Theme;
import org.cef.CefClient;
import org.cef.OS;
import org.cef.browser.CefBrowser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.im.InputMethodRequests;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class JBCefOsrComponent extends JPanel {

	static final Dimension DEF_PREF_SIZE = new Dimension(800, 600);

	static final int RESIZE_DELAY_MS = 100;

	private volatile JBCefOsrHandler myRenderHandler;
	private volatile CefBrowser myBrowser;

	private final @Nonnull JBCefInputMethodAdapter myInputMethodAdapter = new JBCefInputMethodAdapter(this);

	private final @Nonnull AtomicLong myScheduleResizeMs = new AtomicLong(-1);
	private @Nullable TimedTaskQueue myResizeTimedTaskQueue;

	private final @Nonnull TimedTaskQueue myGraphicsConfigurationTimedTaskQueue = new TimedTaskQueue(
			TimedTaskQueue.ThreadToUse.SWING_THREAD);
	AtomicBoolean myScaleInitialized = new AtomicBoolean(false);

	public JBCefOsrComponent() {
		setPreferredSize(DEF_PREF_SIZE);
		setBackground(Theme.current().getBackgroundColor());

		enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK
				| AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.INPUT_METHOD_EVENT_MASK);
		enableInputMethods(true);

		setFocusable(true);
		setRequestFocusEnabled(true);
		setFocusTraversalKeysEnabled(false);

		addInputMethodListener(myInputMethodAdapter);

		// This delay is a workaround for JBR-7335.
		// After the device configuration is changed, the browser reacts to it whether it receives a notification from the client or not.
		// An additional notification during this time can break the internal state of the browser, which leads to the picture freeze.
		// The purpose of this delay is to give the browser a chance to handle the graphics configuration change before we update the scale on
		// our side.
		// The first graphicsConfiguration call is caused by the adding the browser component and doesn't need to be delayed.
		// Further calls might be caused by the hardware setup or resolution changes.
		addPropertyChangeListener("graphicsConfiguration", e -> {
			myGraphicsConfigurationTimedTaskQueue.cancelAllRequests();
			if (myScaleInitialized.get()) {
				myGraphicsConfigurationTimedTaskQueue.addRequest(this::onGraphicsConfigurationChanged, 1000);
			} else {
				onGraphicsConfigurationChanged();
				myScaleInitialized.set(true);
			}
		});
	}

	public void setBrowser(@Nonnull CefBrowser browser) {
		myBrowser = browser;
		myInputMethodAdapter.setBrowser(browser);
	}

	public void setRenderHandler(@Nonnull JBCefOsrHandler renderHandler) {
		myRenderHandler = renderHandler;

		myRenderHandler.addCaretListener(myInputMethodAdapter);

		addAncestorListener(new AncestorListener() {
			@Override public void ancestorAdded(AncestorEvent event) {
				if (isShowing())
					myRenderHandler.setLocationOnScreen(getLocationOnScreen());
			}

			@Override public void ancestorRemoved(AncestorEvent event) {

			}

			@Override public void ancestorMoved(AncestorEvent event) {

			}
		});

		try {
			myRenderHandler.setLocationOnScreen(getLocationOnScreen());
		} catch (IllegalComponentStateException t) {
			// The component isn't shown
		}

		addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				myRenderHandler.setLocationOnScreen(getLocationOnScreen());
			}
		});
	}

	@Override public void addNotify() {
		super.addNotify();
		myResizeTimedTaskQueue = new TimedTaskQueue(TimedTaskQueue.ThreadToUse.POOLED_THREAD);

		if (!CefClient.isNativeBrowserCreationStarted(myBrowser)) {
			myBrowser.createImmediately();
		}
	}

	@Override public void removeNotify() {
		super.removeNotify();

		myGraphicsConfigurationTimedTaskQueue.cancelAllRequests();
		myScaleInitialized.set(false);
	}

	@Override protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		myRenderHandler.paint((Graphics2D) g);
	}

	@SuppressWarnings("deprecation") @Override public void reshape(int x, int y, int w, int h) {
		super.reshape(x, y, w, h);
		final long timeMs = System.currentTimeMillis();
		if (myResizeTimedTaskQueue != null) {
			if (myResizeTimedTaskQueue.isEmpty())
				myScheduleResizeMs.set(timeMs);
			myResizeTimedTaskQueue.cancelAllRequests();
			if (timeMs - myScheduleResizeMs.get() > RESIZE_DELAY_MS)
				myBrowser.wasResized(0, 0);
			else
				myResizeTimedTaskQueue.addRequest(() -> {
					// In OSR width and height are ignored. The view size will be requested from CefRenderHandler.
					myBrowser.wasResized(0, 0);
				}, RESIZE_DELAY_MS);
		}
	}

	@Override public InputMethodRequests getInputMethodRequests() {
		return myInputMethodAdapter;
	}

	@SuppressWarnings("DuplicatedCode") @Override protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		if (e.isConsumed()) {
			return;
		}

		myBrowser.sendMouseEvent(
				new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), e.getX(), e.getY(),
						e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(), e.isPopupTrigger(), e.getButton()));

		if (e.getID() == MouseEvent.MOUSE_PRESSED) {
			requestFocusInWindow();
		}

		repaint();
	}

	@Override protected void processMouseWheelEvent(MouseWheelEvent e) {
		super.processMouseWheelEvent(e);

		if (e.isConsumed())
			return;

		double val = e.getPreciseWheelRotation() * 120;
		if (OS.isLinux() || OS.isMacintosh()) {
			val *= -1;
		}

		myBrowser.sendMouseWheelEvent(
				new MouseWheelEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), e.getX(), e.getY(),
						e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(), e.isPopupTrigger(), e.getScrollType(),
						e.getScrollAmount(), (int) val, val));
	}

	@SuppressWarnings("DuplicatedCode") @Override protected void processMouseMotionEvent(MouseEvent e) {
		super.processMouseMotionEvent(e);

		myBrowser.sendMouseEvent(
				new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), e.getX(), e.getY(),
						e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(), e.isPopupTrigger(), e.getButton()));
	}

	@Override protected void processKeyEvent(KeyEvent e) {
		super.processKeyEvent(e);
		myBrowser.sendKeyEvent(e);
	}

	private void onGraphicsConfigurationChanged() {
		double oldDensity = myRenderHandler.getPixelDensity();
		double pixelDensity = JCefAppConfig.getDeviceScaleFactor(this);
		myRenderHandler.setPixelDensity(pixelDensity);
		if (oldDensity != pixelDensity) {
			myBrowser.notifyScreenInfoChanged();
		}
	}

}
